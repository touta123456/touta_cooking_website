import axios from 'axios';
import authHeader from './auth.header';

const API_URL = 'https://localhost:8443/api/chef/';

class ChefService {

  getChefBoard() {
    return axios.get(API_URL + 'myrecipes', { headers: authHeader() });
  }

}

export default new ChefService();
