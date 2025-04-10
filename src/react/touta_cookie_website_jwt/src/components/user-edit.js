import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';
import { useForm, Controller } from 'react-hook-form';
import axios from 'axios';

const UserEdit = () => {
  const initialFormState = {
    firstName: '',
	lastName: '',
	userName: '',
	email: ''
  };
  const [user, setUser] = useState(initialFormState);
  const navigate = useNavigate();
  const { id } = useParams();
  
  const [roles, setRoles] = useState([]); // Liste des rôles récupérés depuis l'API
  const [selectedRole, setSelectedRole] = useState(""); // Rôle sélectionné par l'utilisateur
  
  const options = {}

  const token = localStorage.getItem('user') && JSON.parse(localStorage.getItem('user')).accessToken;
    
    const headers = {
      ...options.headers,
      ...(token && { Authorization: `Bearer ${token}` })
    };
  
  useEffect(() => {
	

	
    if (id !== 'new') {
      fetch(`/api/users/user/${id}`, { ...options, headers })
        .then(response => response.json())
        .then(data => {
			setUser(data);
			if (data.role) {
				setSelectedRole(data.role.id.toString());
			}
		});
		
    }
  }, [id, setUser]);

  
  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const response = await axios.get("/api/roles/roles", { ...options, headers });
        setRoles(response.data);
		
      } catch (error) {
        console.error("Erreur lors de la récupération des rôles :", error);
      }
    };

    fetchRoles();
  }, []);
  
  useEffect(() => {
    if (roles.length > 0 && (! user.role)) {
      const userRole = roles.find(role => role.libelle === "USER");
      setSelectedRole(userRole?.id || "");
    }
  }, [roles]);
  
  const handleChange = (event) => {
    const { name, value } = event.target
	
    setUser({ ...user, [name]: value })
  }

  // Gestionnaire pour la sélection d'un rôle
  const handleRoleChange = (event) => {
    setSelectedRole(event.target.value);
  };
  
  
  const { control, handleSubmit, setError, formState: { errors } } = useForm();
  
  const onSubmit = async (data) => {

	try{

		const userData = {
		  ...user,
		  role:{id: selectedRole} // Ajouter le rôle sélectionné
		};
		
		if(user.id){
			await axios.put('/api/users/user/'+user.id, userData, { ...options, headers });
		}
		else{
			await axios.post('/api/users/user', userData, { ...options, headers });
		}
		setUser(initialFormState);
		navigate('/api/users/users');
	}
	catch (error) {
	     if (error.response?.status === 400) {

	       // Traitement des erreurs de validation
	       Object.entries(error.response.data).forEach(([field, message]) => {
	         setError(field, { type: 'server', message });
	       });
	     }
	   }
  }

  const title = <h2>{user.id ? 'Edit User' : 'Add User'}</h2>;
  
  return (<div>
      <Container>
        {title}
        <Form onSubmit={handleSubmit(onSubmit)}>
		
          <FormGroup>
            <Label for="firstName">First Name</Label><br/>
			<Controller
			  name="firstName"
			  control={control}
			  render={({ field }) => <input {...field} type="text" name="firstName" id="firstName" value={user.firstName || ''}
			                     onChange={handleChange} autoComplete="name"/>}
			/>
				   {errors.firstName && <span style={{color: 'red'}}>{errors.firstName.message}</span>}
          </FormGroup>
		  
		  <FormGroup>
		    <Label for="lastName">Last Name</Label><br/>
			<Controller
			  name="lastName"
			  control={control}
			  render={({ field }) => <input {...field} type="text" name="lastName" id="lastName" value={user.lastName || ''}
			  		           onChange={handleChange} autoComplete="name"/>}
			/>
				   {errors.lastName && <span style={{color: 'red'}}>{errors.lastName.message}</span>}
		  </FormGroup>
		  
		  <FormGroup>
		    <Label for="userName">User Name</Label><br/>
		  <Controller
		    name="userName"
		    control={control}
		    render={({ field }) => <input {...field} type="text" name="userName" id="userName" value={user.userName || ''}
		    		           onChange={handleChange} autoComplete="name"/>}
		  />
		  	   {errors.userName && <span style={{color: 'red'}}>{errors.userName.message}</span>}
		  </FormGroup>

		  <FormGroup>
            <Label for="email">Email</Label><br/>
			<Controller
			  name="email"
			  control={control}
			  render={({ field }) => <input {...field} type="text" name="email" id="email" value={user.email || ''}
			  			         onChange={handleChange} autoComplete="name"/>}
			/>
				   {errors.email && <span style={{color: 'red'}}>{errors.email.message}</span>}
          </FormGroup>
		  
		  <FormGroup>
		    <Label for="role">Role</Label><br/>
			<select id="role" value={selectedRole} onChange={handleRoleChange}>
			  {roles.map((role) => (
			    <option key={role.id} value={role.id}>
			      {role.libelle}
			    </option>
			  ))}
			</select>
		  </FormGroup>

          <FormGroup>
            <Button color="primary" type="submit">Save</Button>{' '}
            <Button color="secondary" tag={Link} to="/api/users/users">Cancel</Button>
          </FormGroup>
		  
        </Form>
      </Container>
    </div>
  )
};

export default UserEdit;