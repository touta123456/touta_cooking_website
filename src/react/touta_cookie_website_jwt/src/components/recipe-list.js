import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Button, Card, CardBody, CardText, CardTitle, Input, ListGroup, ListGroupItem } from 'reactstrap';

const RecipeList = () => {
  const [recipes, setRecipes] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
	
	const options = {}

	const token = localStorage.getItem('user') && JSON.parse(localStorage.getItem('user')).accessToken;
	  
	  const headers = {
	    ...options.headers,
	    ...(token && { Authorization: `Bearer ${token}` })
	  };

    fetch('/api/recipes/all', { ...options, headers })
      .then(response => response.json())
      .then(data => setRecipes(data));
  }, []);

  const filteredRecipes = recipes.filter(recipe => {
    const searchKeywords = searchTerm.toLowerCase().split(' ');
    return searchKeywords.every(keyword => 
      recipe.keywords?.some(k => k.toLowerCase().includes(keyword))
    );
  });

  return (
    <div className="container mt-4">
      <h2>Chef's recipes</h2>
      
      <Input
        type="text"
        placeholder="Search by key words..."
        className="mb-4"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />

      <div className="row">
        {filteredRecipes.map(recipe => (
          <div className="col-md-4 mb-4" key={recipe.id}>
            <Card>
              <CardBody>
                <CardTitle tag="h5">{recipe.title}</CardTitle>
				<CardText>
				  <strong>Author :</strong> {recipe.author?.firstName} {recipe.author?.lastName || ''}
				</CardText>
                <CardText>
                  <strong>Key Words :</strong> {recipe.keywords?.join(', ') || '-'}
                </CardText>
                <Link to={`/api/recipes/recipe/${recipe.id}`} className="btn btn-primary">
                  View
                </Link>
              </CardBody>
            </Card>
          </div>
        ))}
      </div>
    </div>
  );
};

export default RecipeList;
