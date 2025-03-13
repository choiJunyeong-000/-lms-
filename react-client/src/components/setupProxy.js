const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(
    '/api',
    createProxyMiddleware({
      target: 'https://www.kocw.net',
      changeOrigin: true,
      pathRewrite: {
        '^/api': '', // '/api' 경로를 제거하고 실제 API 경로로 변경
      },
    })
  );
};