import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'https://localhost:8443'
});

axiosInstance.interceptors.request.use(config => {
  const user = JSON.parse(localStorage.getItem('user'));
  if (user?.accessToken) {
    config.headers.Authorization = `Bearer ${user.accessToken}`;
  }
  return config;
});

export default axiosInstance;