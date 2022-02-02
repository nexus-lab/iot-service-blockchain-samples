function HomePage() {
  return <div>Hello World</div>;
}

export function getStaticProps() {
  return {
    props: {
      title: 'Home',
    },
  };
}

export default HomePage;
