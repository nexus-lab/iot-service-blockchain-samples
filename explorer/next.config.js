module.exports = {
  async redirects() {
    return [
      {
        source: '/',
        destination: '/devices',
        permanent: true,
      },
    ];
  },
};
