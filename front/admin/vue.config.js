const path = require('path')

function resolve(dir) {
    return path.join(__dirname, dir)
}

function publicPath() {
    if (process.env.NODE_ENV === 'production') {
        return '././'
    } else {
        return '/'
    }
}

module.exports = {
    publicPath: publicPath(),

    configureWebpack: {
        resolve: {
            alias: {
                '@': resolve('src')
            }
        }
    },

    lintOnSave: false,

    devServer: {
        host: '0.0.0.0',
        port: 8082, // ✅ dev server on 8082 (backend is 8081)
        hot: true,
        https: false,

        // ✅ proxy /api -> Spring Boot 8081
        proxy: {
            '/api': {
                target: 'http://localhost:8081',
                changeOrigin: true,
                secure: false
                // no pathRewrite needed because backend is already /api/**
            }
        }
    },

    chainWebpack(config) {
        config.module
            .rule('svg')
            .exclude.add(resolve('src/icons'))
            .end()

        config.module
            .rule('icons')
            .test(/\.svg$/)
            .include.add(resolve('src/icons'))
            .end()
            .use('svg-sprite-loader')
            .loader('svg-sprite-loader')
            .options({
                symbolId: 'icon-[name]'
            })
            .end()
    }
}
