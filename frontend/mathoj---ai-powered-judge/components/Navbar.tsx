import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { Calculator, User, LogOut, Menu, PlusCircle } from 'lucide-react';
import { authService } from '../services/api';

const Navbar: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const username = localStorage.getItem('username');
  const isLoggedIn = !!localStorage.getItem('token');
  const [isMenuOpen, setIsMenuOpen] = React.useState(false);

  const handleLogout = () => {
    authService.logout();
    navigate('/login');
  };

  const navLinks = [
    { name: 'Problems', path: '/' },
    { name: 'Submissions', path: '/submissions' },
  ];

  return (
    <nav className="bg-vnoi-700 text-white shadow-md sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <div className="flex items-center gap-2">
            <Link to="/" className="flex items-center gap-2 text-xl font-bold tracking-wide">
              <Calculator className="w-8 h-8 text-white" />
              <span>MathOJ</span>
            </Link>
          </div>

          {/* Desktop Nav */}
          <div className="hidden md:block">
            <div className="ml-10 flex items-baseline space-x-4">
              {navLinks.map((link) => (
                <Link
                  key={link.name}
                  to={link.path}
                  className={`px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                    location.pathname === link.path
                      ? 'bg-vnoi-800 text-white'
                      : 'text-gray-200 hover:bg-vnoi-500'
                  }`}
                >
                  {link.name}
                </Link>
              ))}
              {isLoggedIn && (
                <Link
                  to="/create-problem"
                  className={`flex items-center gap-1 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                    location.pathname === '/create-problem'
                      ? 'bg-vnoi-800 text-white'
                      : 'text-gray-200 hover:bg-vnoi-500'
                  }`}
                >
                  <PlusCircle className="w-4 h-4" />
                  Create Problem
                </Link>
              )}
            </div>
          </div>

          {/* Auth Buttons */}
          <div className="hidden md:flex items-center gap-4">
            {isLoggedIn ? (
              <div className="flex items-center gap-4">
                <span className="flex items-center gap-2 text-sm font-medium">
                  <User className="w-4 h-4" />
                  {username}
                </span>
                <button
                  onClick={handleLogout}
                  className="flex items-center gap-1 bg-red-600 hover:bg-red-700 px-3 py-1.5 rounded text-sm transition-colors"
                >
                  <LogOut className="w-4 h-4" />
                  Logout
                </button>
              </div>
            ) : (
              <div className="flex items-center gap-2">
                <Link
                  to="/login"
                  className="bg-white text-vnoi-700 hover:bg-gray-100 px-4 py-2 rounded-md text-sm font-medium transition-colors"
                >
                  Login
                </Link>
                <Link
                  to="/register"
                  className="border border-white text-white hover:bg-vnoi-500 px-4 py-2 rounded-md text-sm font-medium transition-colors"
                >
                  Register
                </Link>
              </div>
            )}
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden flex items-center">
             <button onClick={() => setIsMenuOpen(!isMenuOpen)} className="text-white hover:bg-vnoi-500 p-2 rounded">
               <Menu />
             </button>
          </div>
        </div>
      </div>
      
      {/* Mobile Menu */}
      {isMenuOpen && (
        <div className="md:hidden bg-vnoi-800 pb-4 px-2 pt-2">
           {navLinks.map((link) => (
             <Link
               key={link.name}
               to={link.path}
               onClick={() => setIsMenuOpen(false)}
               className="block px-3 py-2 rounded-md text-base font-medium text-white hover:bg-vnoi-500"
             >
               {link.name}
             </Link>
           ))}
           {isLoggedIn && (
              <Link
                to="/create-problem"
                onClick={() => setIsMenuOpen(false)}
                className="flex items-center gap-2 px-3 py-2 rounded-md text-base font-medium text-white hover:bg-vnoi-500"
              >
                <PlusCircle className="w-4 h-4" />
                Create Problem
              </Link>
           )}
             {isLoggedIn ? (
              <button
                onClick={handleLogout}
                className="w-full text-left block px-3 py-2 rounded-md text-base font-medium text-red-300 hover:bg-red-900 mt-2"
              >
                Logout ({username})
              </button>
            ) : (
              <div className="mt-4 space-y-2 px-3">
                 <Link to="/login" className="block w-full text-center bg-white text-vnoi-700 py-2 rounded">Login</Link>
              </div>
            )}
        </div>
      )}
    </nav>
  );
};

export default Navbar;