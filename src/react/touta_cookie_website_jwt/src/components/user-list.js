import React, { useEffect, useState } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import { Link } from 'react-router-dom';

const UserList = () => {

  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  
  const options = {}

  const token = localStorage.getItem('user') && JSON.parse(localStorage.getItem('user')).accessToken;
    
    const headers = {
      ...options.headers,
      ...(token && { Authorization: `Bearer ${token}` })
    };
  
  useEffect(() => {
    setLoading(true);

    fetch('users', { ...options, headers })
      .then(response => response.json())
      .then(data => {
        setUsers(data);
        setLoading(false);
      })
  }, []);

  const remove = async (id) => {
    await fetch(`/api/users/user/${id}`, {
      method: 'DELETE',
      headers: headers
    }).then(() => {
      let updatedUsers = [...users].filter(i => i.id !== id);
      setUsers(updatedUsers);
    });
  }

  if (loading) {
    return <p>Loading...</p>;
  }

  
  
  const userList = users.map(user => {
    return <tr key={user.id}>
      <td style={{whiteSpace: 'nowrap'}}>{user.firstName} {user.lastName}</td>
	  <td style={{whiteSpace: 'nowrap'}}>{user.userName}</td>
	        <td>{user.email}</td>
      <td>{user.role.libelle}</td>
      <td>
        <ButtonGroup>
          <Button size="sm" color="primary" tag={Link} to={"/api/users/user/" + user.id}>Edit</Button>
          <Button size="sm" color="danger" onClick={() => remove(user.id)} disabled={user.role === "ADMIN"}>Delete</Button>
        </ButtonGroup>
      </td>
    </tr>
  });

  return (
    <div>

      <Container fluid>
	  <Table className="mt-5">
	    <tbody>
		  <tr>
		    <td>
			  <h3>Touta Cooking Admin Users Board</h3>
			</td>
			<td>
			  <Button color="success" tag={Link} to={"/api/users/user/"}>Add User</Button>
			</td>
						
		  </tr>
	    </tbody>
	  </Table>
	  

        
        <Table className="mt-5">
          <thead>
          <tr>
            <th width="20%">Full Name</th>
			<th width="20%">User Name</th>
			<th width="20%">Email</th>
            <th>Role</th>
            <th width="10%">Actions</th>
          </tr>
          </thead>
          <tbody>
          {userList}
          </tbody>
        </Table>
      </Container>
    </div>
  );
};

export default UserList;