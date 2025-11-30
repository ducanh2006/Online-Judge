import React from 'react';
import { HashRouter, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import ProblemList from './components/ProblemList';
import ProblemDetail from './components/ProblemDetail';
import SubmissionList from './components/SubmissionList';
import Login from './components/Login';
import Register from './components/Register';
import CreateProblem from './components/CreateProblem';

const App: React.FC = () => {
  return (
    <HashRouter>
      <div className="min-h-screen flex flex-col font-sans bg-gray-50">
        <Navbar />
        <main className="flex-grow">
          <Routes>
            <Route path="/" element={<ProblemList />} />
            <Route path="/problem/:id" element={<ProblemDetail />} />
            <Route path="/create-problem" element={<CreateProblem />} />
            <Route path="/submissions" element={<SubmissionList />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
        <footer className="bg-white border-t border-gray-200 mt-12 py-8">
          <div className="max-w-7xl mx-auto px-4 text-center text-gray-500 text-sm">
            <p>&copy; {new Date().getFullYear()} MathOJ. AI-Powered Mathematics Judge.</p>
          </div>
        </footer>
      </div>
    </HashRouter>
  );
};

export default App;