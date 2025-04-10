import React, { Component } from "react";
import { Routes, Route, Link } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import "./App.css";

import AuthService from "./services/auth.service";

import Login from "./components/login.component";
import Register from "./components/register.component";
import Home from "./components/home.component";
import Profile from "./components/profile.component";
import BoardUser from "./components/board-user.component";
import RecipeList from './components/recipe-list';
import RecipeDetails from './components/recipe-details';
import MyRecipesList from "./components/my-recipes-list";
import MyRecipeEdit from "./components/my-recipe-edit";
import UserList from "./components/user-list";
import UserEdit from "./components/user-edit";

// import AuthVerify from "./common/auth-verify";
import EventBus from "./common/EventBus";
import axiosInstance from './services/axios.instance';
import authHeader from "./services/auth.header";

axiosInstance.interceptors.request.use(config => {
  const authHeaders = authHeader();
  if (authHeaders.Authorization) {
    config.headers.Authorization = authHeaders.Authorization;
  }
  return config;
});

axiosInstance.interceptors.request.use(config => {
  console.log('Headers envoyés:', config.headers); // Debug
  return config;
});

class App extends Component {
  constructor(props) {
    super(props);
    this.logOut = this.logOut.bind(this);

    this.state = {
      showChef: false,
      showAdminBoard: false,
	  showUser: false,
      currentUser: undefined,
    };
  }

  componentDidMount() {
    const user = AuthService.getCurrentUser();

    if (user) {
      this.setState({
        currentUser: user,
        showChef: user.role === "ROLE_CHEF",
        showAdminBoard: user.role === "ROLE_ADMIN",
		showUser: user.role === "ROLE_USER"
      });
    }
    
    EventBus.on("logout", () => {
      this.logOut();
    });
  }

  componentWillUnmount() {
    EventBus.remove("logout");
  }

  logOut() {
    AuthService.logout();
    this.setState({
      showChef: false,
      showAdminBoard: false,
	  showUser: false,
      currentUser: undefined,
    });
  }
  
  
  render() {
    const { currentUser, showChef, showUser, showAdminBoard } = this.state;

    return (
      <div>
        <nav className="navbar navbar-expand navbar-dark bg-dark">
          <Link to={"/"} className="navbar-brand">
            Touta Cooking
          </Link>
          <div className="navbar-nav mr-auto">
            <li className="nav-item">
              <Link to={"/home"} className="nav-link">
                Home
              </Link>
            </li>

			{showUser && (
			  <li className="nav-item">
			    <Link to={"/api/recipes/all"} className="nav-link">
			      Recipes
			    </Link>
			  </li>
			)}
			
            {showChef && (
              <li className="nav-item">
                <Link to={"/api/chef/myrecipes"} className="nav-link">
                  My recipes
                </Link>
              </li>
            )}

            {showAdminBoard && (
              <li className="nav-item">
                <Link to={"/api/users/users"} className="nav-link">
                  Admin Board
                </Link>
              </li>
            )}

          </div>

          {currentUser ? (
            <div className="navbar-nav ml-auto">
              <li className="nav-item">
                <Link to={"/profile"} className="nav-link">
                  {currentUser.firstName} {currentUser.lastName}
                </Link>
              </li>
              <li className="nav-item">
                <a href="/login" className="nav-link" onClick={this.logOut}>
                  LogOut
                </a>
              </li>
            </div>
          ) : (
            <div className="navbar-nav ml-auto">
              <li className="nav-item">
                <Link to={"/login"} className="nav-link">
                  Login
                </Link>
              </li>

              <li className="nav-item">
                <Link to={"/register"} className="nav-link">
                  Sign Up
                </Link>
              </li>
            </div>
          )}
        </nav>

        <div className="container mt-3">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/home" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/user" element={<BoardUser />} />
			<Route path="/api/recipes/all" element={<RecipeList />} />
			<Route path="/api/recipes/recipe/:id" element={<RecipeDetails />} />
            <Route path="/api/chef/myrecipes" element={<MyRecipesList />} />
			<Route path='/api/chef/recipe/:id' element={<MyRecipeEdit/>}/>
			<Route path='/api/chef/recipe/' element={<MyRecipeEdit/>}/>
            <Route path="/api/users/users" element={<UserList />} />
			<Route path='/api/users/user/:id' element={<UserEdit/>}/>
			<Route path='/api/users/user/' element={<UserEdit/>}/>
          </Routes>
        </div>

        {/* <AuthVerify logOut={this.logOut}/> */}
      </div>
    );
  }
}

export default App;
