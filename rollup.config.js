const path = require('path');
const globby = require('globby');
const terser = require("rollup-plugin-terser");

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
    const plugins = [terser.terser()];

    return paths.map(file => ({
        input: file,
        output: {
            file: getOutputFileName(file),
            name: getModuleName(file),
            format: 'iife',
            sourceMap: 'inline'
        },
        plugins: plugins
    }));
}

export default getConfig();
