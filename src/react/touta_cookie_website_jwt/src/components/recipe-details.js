import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Button, Card, CardBody, CardText, CardTitle, Form, Input, ListGroup, ListGroupItem } from 'reactstrap';

const RecipeDetails = () => {
  const { id } = useParams();
  const [recipe, setRecipe] = useState(null);
  const [commentText, setCommentText] = useState('');

  useEffect(() => {
	
	const options = {}

	const token = localStorage.getItem('user') && JSON.parse(localStorage.getItem('user')).accessToken;
	  
	  const headers = {
	    ...options.headers,
	    ...(token && { Authorization: `Bearer ${token}` })
	  };
	
    fetch(`/api/recipes/recipe/${id}`, { ...options, headers })
      .then(response => response.json())
      .then(data => setRecipe(data));
  }, [id]);

  const handleCommentSubmit = async (e) => {
    e.preventDefault();
    
    const token = JSON.parse(localStorage.getItem('user'))?.accessToken;
    
    const response = await fetch(`/api/recipes/comment/${id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`
      },
      body: JSON.stringify({ content: commentText })
    });

    if (response.ok) {
      const newComment = await response.json();
      setRecipe(prev => ({
        ...prev,
        comments: [...prev.comments, newComment]
      }));
      setCommentText('');
    }
  };

  if (!recipe) return <div>Chargement...</div>;

  return (
    <div className="container mt-4">
      <Card className="mb-4">
        <CardBody>
          <CardTitle tag="h2">{recipe.title}</CardTitle>
		  <CardText>
		    <strong>Author :</strong> {recipe.author?.firstName} {recipe.author?.lastName || ''}
		  </CardText>
		  <CardText>
		    <strong>Ingredients :</strong>
		    <div>
		      {recipe.ingredients.split('\n').map((line, index) => (
		        <span key={index}>
		          {line}
		          <br />
		        </span>
		      ))}
		    </div>
		  </CardText>
          <CardText>
            <strong>Key words :</strong> {recipe.keywords?.join(', ') || 'Aucun'}
          </CardText>
        </CardBody>
      </Card>

      <h3>Comments ({recipe.comments?.length || 0})</h3>
      
      <Form onSubmit={handleCommentSubmit} className="mb-4">
        <Input
          type="textarea"
          value={commentText}
          onChange={(e) => setCommentText(e.target.value)}
          placeholder="Ajouter un commentaire..."
          required
        />
        <Button color="primary" className="mt-2">Publish</Button>
      </Form>

      <ListGroup>
        {recipe.comments?.map(comment => (
          <ListGroupItem key={comment.id} className="mb-3">
            <div className="d-flex justify-content-between align-items-start">
              <div>
                <strong>
                  {comment.user?.firstName} {comment.user?.lastName}
                </strong>
                <p className="mb-0">{comment.content}</p>
              </div>
			  <div>
	              <small className="text-muted">
	                {new Date(comment.datePublication).toLocaleString()}
	              </small>
			  </div>
            </div>
          </ListGroupItem>
        ))}
      </ListGroup>
    </div>
  );
};

export default RecipeDetails;
