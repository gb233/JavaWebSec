import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import sass from 'sass-embedded'
import eslint from 'vite-plugin-eslint'
import WindiCSS from 'vite-plugin-windicss'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import Icons from 'unplugin-icons/vite'
import IconsResolver from 'unplugin-icons/resolver'
import { viteMockServe } from 'vite-plugin-mock'

// https://vitejs.dev/config/
export default defineConfig(({ mode, command }) => {
  const isDev = mode === 'development'
  const isProd = mode === 'production'

  // 开发环境日志配置
  if (isDev) {
    console.log('🚀 启动开发服务器...')
    console.log('📁 项目根目录:', process.cwd())
    console.log('🌍 模式:', mode)
    console.log('⚡ 命令:', command)
  }

  return {
    plugins: [
      vue(),
      
      // ESLint检查（暂时禁用以避免配置问题）
      // eslint({
      //   include: ['src/**/*.vue', 'src/**/*.ts', 'src/**/*.tsx'],
      //   exclude: ['node_modules', 'dist'],
      //   cache: false
      // }),

      // WindiCSS
      WindiCSS(),


      // 自动导入
      AutoImport({
        imports: [
          'vue',
          'vue-router',
          'pinia',
          '@vueuse/core'
        ],
        resolvers: [
          ElementPlusResolver(),
          IconsResolver({
            prefix: 'Icon'
          })
        ],
        dts: 'src/types/auto-imports.d.ts',
        eslintrc: {
          enabled: true,
          filepath: './.eslintrc-auto-import.json',
          globalsPropValue: true
        }
      }),

      // 组件自动导入
      Components({
        resolvers: [
          ElementPlusResolver({
            importStyle: 'sass'
          }),
          IconsResolver({
            enabledCollections: ['ep', 'carbon', 'lucide']
          })
        ],
        dts: 'src/types/components.d.ts'
      }),

      // 图标
      Icons({
        autoInstall: true
      }),

      // Mock服务（仅开发环境）
      isDev && viteMockServe({
        mockPath: 'mock',
        localEnabled: true,
        prodEnabled: false,
        injectCode: `
          import { setupProdMockServer } from '../mock/_createProductionServer'
          setupProdMockServer()
        `,
        logger: true
      }),

      // 开发环境调试插件
      isDev && {
        name: 'dev-debug',
        configureServer(server) {
          server.middlewares.use('/debug', (req, res, next) => {
            console.log('🔍 调试请求:', req.url)
            res.setHeader('Content-Type', 'application/json')
            res.end(JSON.stringify({
              url: req.url,
              method: req.method,
              headers: req.headers,
              timestamp: new Date().toISOString()
            }))
          })
        }
      }
    ].filter(Boolean),

    // 路径解析
    resolve: {
      alias: {
        '@': resolve(__dirname, 'src'),
        '@components': resolve(__dirname, 'src/components'),
        '@views': resolve(__dirname, 'src/views'),
        '@utils': resolve(__dirname, 'src/utils'),
        '@api': resolve(__dirname, 'src/api'),
        '@stores': resolve(__dirname, 'src/stores'),
        '@assets': resolve(__dirname, 'src/assets'),
        '@styles': resolve(__dirname, 'src/styles'),
        '@types': resolve(__dirname, 'src/types'),
        '@hooks': resolve(__dirname, 'src/hooks'),
        '@layouts': resolve(__dirname, 'src/layouts'),
        '@router': resolve(__dirname, 'src/router')
      }
    },

    // CSS预处理器
    css: {
      preprocessorOptions: {
        scss: {
          implementation: sass,
          additionalData: `
            @use "@/styles/element/index.scss" as *;
            @use "@/styles/variables.scss" as *;
          `
        }
      }
    },

    // 开发服务器配置
    server: {
      host: '0.0.0.0',
      port: 3000,
      open: false,
      cors: true,
      strictPort: false,
      hmr: {
        overlay: true
      },
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          ws: true,
          timeout: 20000,
          rewrite: (path) => path.replace(/^\/api/, '/api')
        },
        // WebSocket代理（如果需要）
        '/ws': {
          target: 'ws://localhost:8080',
          ws: true
        }
      }
    },

    // 构建配置
    build: {
      target: 'es2015',
      outDir: 'dist',
      assetsDir: 'assets',
      minify: isProd ? 'esbuild' : false,
      sourcemap: isDev,
      
      // 分包策略
      rollupOptions: {
        output: {
          chunkFileNames: 'assets/js/[name]-[hash].js',
          entryFileNames: 'assets/js/[name]-[hash].js',
          assetFileNames: (assetInfo) => {
            const info = assetInfo.name?.split('.') || []
            let extType = info[info.length - 1]
            
            if (/\.(mp4|webm|ogg|mp3|wav|flac|aac)(\?.*)?$/i.test(assetInfo.name || '')) {
              extType = 'media'
            } else if (/\.(png|jpe?g|gif|svg)(\?.*)?$/i.test(assetInfo.name || '')) {
              extType = 'img'
            } else if (/\.(woff2?|eot|ttf|otf)(\?.*)?$/i.test(assetInfo.name || '')) {
              extType = 'fonts'
            }
            
            return `assets/${extType}/[name]-[hash].[ext]`
          },
          
          // 手动分包
          manualChunks: {
            'vue-vendor': ['vue', 'vue-router', 'pinia'],
            'element-plus': ['element-plus', '@element-plus/icons-vue'],
            'utils': ['axios', 'dayjs', 'lodash-es'],
            'charts': ['echarts', 'vue-echarts'],
            'editor': ['monaco-editor', 'highlight.js', 'marked']
          }
        }
      },

      // Terser配置（生产环境）
      terserOptions: undefined,

      // esbuild压缩配置（生产环境去除调试信息）
      esbuild: isProd ? {
        drop: ['console', 'debugger']
      } : undefined,

      // 构建性能警告阈值
      chunkSizeWarningLimit: 1000
    },

    // 优化配置
    optimizeDeps: {
      include: [
        'vue',
        'vue-router',
        'pinia',
        'element-plus',
        '@element-plus/icons-vue',
        'axios',
        'dayjs',
        'lodash-es',
        'echarts',
        'vue-echarts'
      ]
    },

    // 环境变量
    define: {
      __APP_VERSION__: JSON.stringify(process.env.npm_package_version),
      __BUILD_TIME__: JSON.stringify(new Date().toISOString()),
      __GITHUB_URL__: JSON.stringify(
        process.env.VITE_APP_GITHUB_URL || 'https://github.com/javaweb-security/teaching-system'
      )
    },

    // 预览服务器
    preview: {
      port: 4173,
      host: '0.0.0.0',
      cors: true
    },

    // 实验性功能
    experimental: {
      renderBuiltUrl(filename, { hostType }) {
        if (hostType === 'js') {
          return { js: `/${filename}` }
        } else {
          return { relative: true }
        }
      }
    }
  }
})
