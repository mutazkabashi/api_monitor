// import logo from './logo.svg';
// import './App.css';
import 'primereact/resources/themes/saga-blue/theme.css';
import 'primereact/resources/primereact.min.css';
import 'primeicons/primeicons.css';
import 'primeflex/primeflex.css';
import Footer from './components/Footer'
import Signup from './components/Signup'

import MainPage from './components/MainPage';
import GeneralMainPage from './components/GeneralMainPage';
import 'bootstrap/dist/css/bootstrap.min.css'; //*Import React-Bootstrap CSS
import { connect } from 'react-redux'



function App(props) {
  console.log("token" + localStorage.getItem("token"));
  return (
    props.state.token || localStorage.getItem("token") ? <MainPage /> : <GeneralMainPage />
  );
}

//export default App;
const mapStateToProps = state => {
  return {
    state
  }
}

const mapDispatchToProps = dispatch => {
  return {
    dispatch
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(App)
