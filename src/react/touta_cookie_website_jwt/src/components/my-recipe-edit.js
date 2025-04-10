import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';
import { useForm, Controller } from 'react-hook-form';
import axios from 'axios';

const UserEdit = () => {
  const initialFormState = {
    title: '',
    ingredients: '',
    keywords: []
  };
  const [recipe, setRecipe] = useState(initialFormState);
  const [newKeyword, setNewKeyword] = useState('');
  const navigate = useNavigate();
  const { id } = useParams();

  useEffect(() => {
    if (id !== 'new') {
		const options = {}

		const token = localStorage.getItem('user') && JSON.parse(localStorage.getItem('user')).accessToken;
		  
		  const headers = {
		    ...options.headers,
		    ...(token && { Authorization: `Bearer ${token}` })
		  };

      fetch(`/api/chef/recipe/${id}`, { ...options, headers })
        .then(response => response.json())
        .then(data => {
          setRecipe({...data, keywords: data.keywords || []});
        });
    }
  }, [id, setRecipe]);

  const handleAddKeyword = () => {
    if (newKeyword.trim()) {
      setRecipe({
        ...recipe,
        keywords: [...recipe.keywords, newKeyword.trim()]
      });
      setNewKeyword('');
    }
  };

  const handleRemoveKeyword = (indexToRemove) => {
    setRecipe({
      ...recipe,
      keywords: recipe.keywords.filter((_, index) => index !== indexToRemove)
    });
  };

  const { control, handleSubmit, formState: { errors } } = useForm();
  
  const onSubmit = async (data) => {
    const userData = {
      ...recipe
    };
    
    const token = localStorage.getItem('user') && JSON.parse(localStorage.getItem('user')).accessToken;
    const headers = {
      headers: {
        ...(token && { Authorization: `Bearer ${token}` })
      }
    };

    try {
      if(recipe.id) {
        await axios.put(`/api/chef/recipe/${recipe.id}`, userData, headers);
      } else {
        await axios.post('/api/chef/recipe', userData, headers);
      }
      navigate('/api/chef/myrecipes');
    } catch (error) {
      console.error('Submission error:', error);
    }
  };

  const title = <h2>{recipe.id ? 'Edit Recipe' : 'Add Recipe'}</h2>;
  
  return (
    <div>
      <Container>
        {title}
        <Form onSubmit={handleSubmit(onSubmit)}>
          <FormGroup>
            <Label for="title">Title</Label><br/>
            <Controller
              name="title"
              control={control}
              render={({ field }) => (
                <Input 
                  {...field}
                  type="text"
                  id="title"
                  value={recipe.title}
                  onChange={(e) => setRecipe({...recipe, title: e.target.value})}
                  autoComplete="off"
                />
              )}
            />
            {errors.title && <span style={{color: 'red'}}>{errors.title.message}</span>}
          </FormGroup>

          <FormGroup>
            <Label for="ingredients">Ingredients</Label><br/>
            <Controller
              name="ingredients"
              control={control}
              render={({ field }) => (
                <Input 
                  {...field}
                  type="textarea"
                  id="ingredients"
                  value={recipe.ingredients}
                  onChange={(e) => setRecipe({...recipe, ingredients: e.target.value})}
                  autoComplete="off"
				  rows={15}
                />
              )}
            />
            {errors.ingredients && <span style={{color: 'red'}}>{errors.ingredients.message}</span>}
          </FormGroup>

          <FormGroup>
            <Label>Keywords</Label>
            <div className="d-flex align-items-center mb-2">
              <Input
                type="text"
                value={newKeyword}
                onChange={(e) => setNewKeyword(e.target.value)}
                placeholder="Add a keyword"
                className="me-2"
              />
              <Button 
                type="button" 
                onClick={handleAddKeyword}
                color="secondary"
              >
                Add
              </Button>
            </div>
            <div className="d-flex flex-wrap gap-2">
              {recipe.keywords.map((keyword, index) => (
                <div key={index} className="bg-secondary rounded-pill px-3 py-1 d-flex align-items-center">
                  <span className="text-white me-2">{keyword}</span>
                  <Button 
                    type="button" 
                    color="link" 
                    className="p-0 text-white"
                    onClick={() => handleRemoveKeyword(index)}
                  >
                    ×
                  </Button>
                </div>
              ))}
            </div>
          </FormGroup>

          <FormGroup className="mt-4">
            <Button color="primary" type="submit">Save</Button>{' '}
            <Button color="secondary" tag={Link} to="/api/chef/myrecipes">Cancel</Button>
          </FormGroup>
        </Form>
      </Container>
    </div>
  )
};

export default UserEdit;
