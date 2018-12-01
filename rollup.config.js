import path from 'path';

import globby from 'globby';
import fs from 'fs-extra';

import buble from 'rollup-plugin-buble';
import { terser } from 'rollup-plugin-terser';
import resolve from 'rollup-plugin-node-resolve';
import postcss from 'rollup-plugin-postcss';
import replace from 'rollup-plugin-replace';

require('dotenv').config();

const isProdBuild = process.env.NODE_ENV === 'production';
const resourcePath = path.resolve(__dirname, 'src', 'main', 'resources');
const ideaResourcePath = path.resolve(__dirname, 'out', 'production', 'resources');
const sourcePath = path.resolve(resourcePath, 'src', '**/*.mjs');
const destinationPath = path.resolve(resourcePath, 'static', 'js', 'build');

function getOutputFileName(file) {
    const fileName = path.basename(file, path.extname(file));
    const pathDiff = path.dirname(path.relative(path.resolve(resourcePath, 'src'), file));
    return path.resolve(destinationPath, pathDiff, fileName + '.min.js');
}

function getModuleName(file) {
    const fileName = path.basename(file, path.extname(file));
    return fileName.replace(/-/g, '_');
}

function getOutputFileContent(
    outputFileName,
    outputFile,
    outputOptions
) {
    if (typeof outputFile.code === 'string') {
        let source =  outputFile.code;
        if (outputOptions.sourcemap && outputFile.map) {
            const url =
                outputOptions.sourcemap === 'inline'
                    ? outputFile.map.toUrl()
                    : `${path.basename(outputFileName)}.map`;

            // https://github.com/rollup/rollup/blob/master/src/utils/sourceMappingURL.ts#L1
            source += `//# source` + `MappingURL=${url}\n`
        }
        return source
    } else {
        return outputFile
    }
}

function copy() {
    return {
        name: 'copy',
        generateBundle(outputOptions, bundle, isWrite) {
            if (!isWrite)
                return;

            const relativePath = path.relative(resourcePath, outputOptions.file);
            const ideaOutputPath = path.resolve(ideaResourcePath, relativePath);

            const directory = path.dirname(ideaOutputPath);

            return Promise.all(Object.keys(bundle).map(async fileName => {
                const fileEntry = bundle[fileName];

                const fileContent = getOutputFileContent(
                    fileName,
                    fileEntry,
                    outputOptions,
                );

                await fs.mkdirp(directory);
                await fs.writeFile(ideaOutputPath, fileContent);
            }));
        }
    }
}

async function getConfig() {
    const paths = await globby(sourcePath);
    const plugins = [
        postcss({
            minimize: isProdBuild,
            plugins: []
        }),
        buble({
            objectAssign: 'Object.assign',
            jsx: 'h'
        }),
        resolve(),
        replace({
            'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV)
        }),
        copy()
    ];

    if (isProdBuild) {
        plugins.push(terser());
    }

    return paths.map(file => ({
        input: file,
        output: {
            file: getOutputFileName(file),
            name: getModuleName(file),
            format: 'iife',
            sourcemap: true
        },
        plugins: plugins
    }));
}

export default getConfig();
