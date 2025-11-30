import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../services/api';
import { Lock, User, Mail, FileText } from 'lucide-react';

const Register: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    email: '',
    fullName: '',
    displayName: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await authService.register(formData);
      // Automatically login or redirect to login
      navigate('/login');
    } catch (err: any) {
        // Safe check for error response message
      const msg = err.response?.data?.message || err.message || 'Registration failed';
      setError(msg);
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8 bg-white p-8 rounded-xl shadow-lg border border-gray-100">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Create Account
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Already have an account? <Link to="/login" className="font-medium text-vnoi-700 hover:text-vnoi-500">Sign in</Link>
          </p>
        </div>
        <form className="mt-8 space-y-4" onSubmit={handleSubmit}>
            <div className="relative">
              <User className="absolute top-3 left-3 w-5 h-5 text-gray-400" />
              <input
                name="username"
                type="text"
                required
                className="block w-full px-10 py-3 border border-gray-300 rounded-md focus:ring-vnoi-500 focus:border-vnoi-500 sm:text-sm"
                placeholder="Username (min 3 chars)"
                minLength={3}
                value={formData.username}
                onChange={handleChange}
              />
            </div>
            
            <div className="relative">
              <Lock className="absolute top-3 left-3 w-5 h-5 text-gray-400" />
              <input
                name="password"
                type="password"
                required
                className="block w-full px-10 py-3 border border-gray-300 rounded-md focus:ring-vnoi-500 focus:border-vnoi-500 sm:text-sm"
                placeholder="Password (min 6 chars)"
                minLength={6}
                value={formData.password}
                onChange={handleChange}
              />
            </div>

            <div className="relative">
              <Mail className="absolute top-3 left-3 w-5 h-5 text-gray-400" />
              <input
                name="email"
                type="email"
                className="block w-full px-10 py-3 border border-gray-300 rounded-md focus:ring-vnoi-500 focus:border-vnoi-500 sm:text-sm"
                placeholder="Email (Optional)"
                value={formData.email}
                onChange={handleChange}
              />
            </div>

            <div className="relative">
              <FileText className="absolute top-3 left-3 w-5 h-5 text-gray-400" />
              <input
                name="fullName"
                type="text"
                className="block w-full px-10 py-3 border border-gray-300 rounded-md focus:ring-vnoi-500 focus:border-vnoi-500 sm:text-sm"
                placeholder="Full Name"
                value={formData.fullName}
                onChange={handleChange}
              />
            </div>

          {error && (
            <div className="text-red-500 text-sm text-center bg-red-50 p-2 rounded">
              {error}
            </div>
          )}

          <div className="pt-2">
            <button
              type="submit"
              disabled={loading}
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-vnoi-700 hover:bg-vnoi-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-vnoi-500 transition-colors disabled:opacity-70"
            >
              {loading ? 'Creating Account...' : 'Register'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Register;
