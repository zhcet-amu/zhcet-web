import path from 'path';
import globby from 'globby';
import buble from 'rollup-plugin-buble';
import { terser } from 'rollup-plugin-terser';
import resolve from 'rollup-plugin-node-resolve';
import postcss from 'rollup-plugin-postcss'

const isProdBuild = process.env.NODE_ENV === 'production';
const resourcePath = path.resolve(__dirname, 'src', 'main', 'resources');
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

async function getConfig() {
    const paths = await globby(sourcePath);
    const commonPlugins = [
        postcss({
            minimize: isProdBuild,
            plugins: []
        }),
        resolve()
    ];
    const plugins = isProdBuild ?
        [
            ...commonPlugins,
            buble({
                objectAssign: 'Object.assign'
            }),
            terser()
        ] :
        commonPlugins;

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
