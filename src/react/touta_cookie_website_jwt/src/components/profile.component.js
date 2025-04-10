import React, { Component } from "react";
import { Navigate } from "react-router-dom";
import AuthService from "../services/auth.service";

export default class Profile extends Component {
  constructor(props) {
    super(props);

    this.state = {
      redirect: null,
      userReady: false,
      currentUser: { username: "" }
    };
  }

  componentDidMount() {
    const currentUser = AuthService.getCurrentUser();
	
    // if (!currentUser) this.setState({ redirect: "/home" });
    this.setState({ currentUser: currentUser, userReady: true })

  }

  render() {
    const { currentUser } = this.state;

	if(currentUser.role==="ROLE_USER"){
		return <Navigate to={'/api/recipes/all'} />
	}
	else if(currentUser.role==="ROLE_CHEF"){
		return <Navigate to={'/api/chef/myrecipes'} />
	}
	else{ 
		return <Navigate to={'/api/users/users'} />
	}
  }
}
