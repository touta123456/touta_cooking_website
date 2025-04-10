import React, { Component } from "react";
import { Container } from 'reactstrap';
import UserService from "../services/user.service";
import myImage from '../images/cuisine_note_menu.jpeg';

export default class Home extends Component {
  constructor(props) {
    super(props);

    this.state = {
      content: ""
    };
  }


  render() {
    return (
		<Container fluid>
			<center>
				<h2>Welcome to Touta Cooking</h2>
				<img src={myImage} alt="Description" className="img-responsive" />
			</center>
      </Container>
    );
  }
}
