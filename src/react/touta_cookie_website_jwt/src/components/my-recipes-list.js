import React, { useEffect, useState } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
import { Link } from 'react-router-dom';

const MyRecipesList = () => {

  const [recipes, setRecipes] = useState([]);
  const [loading, setLoading] = useState(false);
  
  const options = {}

  const token = localStorage.getItem('user') && JSON.parse(localStorage.getItem('user')).accessToken;
    
    const headers = {
      ...options.headers,
      ...(token && { Authorization: `Bearer ${token}` })
    };

  useEffect(() => {
    setLoading(true);


    fetch('/api/chef/myrecipes', { ...options, headers })
      .then(response => response.json())
      .then(data => {
        setRecipes(data);
        setLoading(false);
      })
  }, []);

  const remove = async (id) => {
    await fetch(`/api/chef/recipe/${id}`, {
      method: 'DELETE',
      headers: headers
    }).then(() => {
      let updatedRecipes = [...recipes].filter(i => i.id !== id);
      setRecipes(updatedRecipes);
    });
  }

  if (loading) {
    return <p>Loading...</p>;
  }

  
  
  const myRecipesList = recipes.map(recipe => {
    return <tr key={recipe.id}>
      <td style={{whiteSpace: 'nowrap'}}>{recipe.title}</td>
	  
	  <td style={{whiteSpace: 'nowrap'}}>{recipe.keywords?.join(', ') || '-'}</td>
      <td>
        <ButtonGroup>
          <Button size="sm" color="primary" tag={Link} to={"/api/chef/recipe/" + recipe.id}>Edit</Button>
          <Button size="sm" color="danger" onClick={() => remove(recipe.id)}>Delete</Button>
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
			  <h3>My recipes</h3>
			</td>
			<td>
			  <Button color="success" tag={Link} to={"/api/chef/recipe/"}>Add Recipe</Button>
			</td>
						
		  </tr>
	    </tbody>
	  </Table>
	  

        
        <Table className="mt-5">
          <thead>
          <tr>
            <th width="20%">Title</th>
			<th width="20%">Key Words</th>
            <th width="10%">Actions</th>
          </tr>
          </thead>
          <tbody>
          {myRecipesList}
          </tbody>
        </Table>
      </Container>
    </div>
  );
};

export default MyRecipesList;