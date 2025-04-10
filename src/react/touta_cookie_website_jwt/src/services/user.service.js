import axios from 'axios';
import authHeader from './auth.header';

const API_URL = 'https://localhost:8443/api/users/';

class UserService {

  getUserBoard() {
    return axios.get(API_URL + 'user', { headers: authHeader() });
  }

  getAdminBoard() {
    return axios.get(API_URL + 'users', { headers: authHeader() });
  }
}

export default new UserService();
