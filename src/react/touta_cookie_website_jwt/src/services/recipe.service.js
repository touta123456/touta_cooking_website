import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'https://localhost:8443/api/recipes/';

class RecipeService {

  getRecipes() {
    return axios.get(API_URL + 'recipes', { headers: authHeader() });
  }

}

export default new RecipeService();
