
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { problemService, subjectService } from '../services/api';
import { SubjectDTO } from '../types';
import { Plus, BookOpen, AlertCircle } from 'lucide-react';

const CreateProblem: React.FC = () => {
  const navigate = useNavigate();
  const [subjects, setSubjects] = useState<SubjectDTO[]>([]);
  const [loading, setLoading] = useState(false);
  
  // Problem Form State
  const [formData, setFormData] = useState({
    title: '',
    difficulty: 1, // Default to 1
    subjectId: '',
    tags: '',
    description: '',
    solution: ''
  });

  // Subject Creation State
  const [isCreatingSubject, setIsCreatingSubject] = useState(false);
  const [newSubjectName, setNewSubjectName] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    fetchSubjects();
  }, []);

  const fetchSubjects = async () => {
    try {
      const data = await subjectService.getAll();
      setSubjects(data);
    } catch (err) {
      console.error("Failed to load subjects", err);
      setError("Failed to load subjects. Please try refreshing.");
    }
  };

  const handleCreateSubject = async () => {
    if (!newSubjectName.trim()) return;
    try {
      const newSubject = await subjectService.create({ name: newSubjectName });
      setSubjects([...subjects, newSubject]);
      setFormData({ ...formData, subjectId: newSubject.id.toString() });
      setNewSubjectName('');
      setIsCreatingSubject(false);
    } catch (err) {
      console.error("Failed to create subject", err);
      alert("Failed to create subject");
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      if (!formData.subjectId) {
        throw new Error("Please select a subject");
      }

      const tagList = formData.tags
        .split(',')
        .map(t => t.trim())
        .filter(t => t.length > 0);

      await problemService.create({
        title: formData.title,
        description: formData.description,
        solution: formData.solution,
        difficulty: formData.difficulty,
        subjectId: parseInt(formData.subjectId),
        tags: tagList
      });

      navigate('/');
    } catch (err: any) {
      console.error("Failed to create problem", err);
      setError(err.message || "Failed to create problem");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="md:flex md:items-center md:justify-between mb-6">
        <div className="flex-1 min-w-0">
          <h2 className="text-2xl font-bold leading-7 text-gray-900 sm:text-3xl sm:truncate flex items-center gap-2">
            <BookOpen className="text-vnoi-700" />
            Create New Problem
          </h2>
        </div>
      </div>

      <div className="bg-white shadow px-4 py-5 sm:rounded-lg sm:p-6">
        <form onSubmit={handleSubmit} className="space-y-6">
          {error && (
            <div className="rounded-md bg-red-50 p-4">
              <div className="flex">
                <div className="flex-shrink-0">
                  <AlertCircle className="h-5 w-5 text-red-400" aria-hidden="true" />
                </div>
                <div className="ml-3">
                  <h3 className="text-sm font-medium text-red-800">{error}</h3>
                </div>
              </div>
            </div>
          )}

          <div className="grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
            
            {/* Title */}
            <div className="sm:col-span-4">
              <label htmlFor="title" className="block text-sm font-medium text-gray-700">
                Problem Title
              </label>
              <div className="mt-1">
                <input
                  type="text"
                  name="title"
                  id="title"
                  required
                  className="shadow-sm focus:ring-vnoi-500 focus:border-vnoi-500 block w-full sm:text-sm border-gray-300 rounded-md p-2 border"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                />
              </div>
            </div>

            {/* Difficulty */}
            <div className="sm:col-span-2">
              <label htmlFor="difficulty" className="block text-sm font-medium text-gray-700">
                Difficulty (1-10)
              </label>
              <div className="mt-1">
                <select
                  id="difficulty"
                  name="difficulty"
                  className="shadow-sm focus:ring-vnoi-500 focus:border-vnoi-500 block w-full sm:text-sm border-gray-300 rounded-md p-2 border"
                  value={formData.difficulty}
                  onChange={(e) => setFormData({ ...formData, difficulty: parseInt(e.target.value) })}
                >
                  {[...Array(10)].map((_, i) => (
                    <option key={i + 1} value={i + 1}>
                      {i + 1}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Subject */}
            <div className="sm:col-span-3">
              <label htmlFor="subject" className="block text-sm font-medium text-gray-700">
                Subject
              </label>
              <div className="mt-1 flex gap-2">
                <select
                  id="subject"
                  name="subject"
                  required
                  className="shadow-sm focus:ring-vnoi-500 focus:border-vnoi-500 block w-full sm:text-sm border-gray-300 rounded-md p-2 border"
                  value={formData.subjectId}
                  onChange={(e) => setFormData({ ...formData, subjectId: e.target.value })}
                >
                  <option value="">Select a Subject...</option>
                  {subjects.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.name}
                    </option>
                  ))}
                </select>
                <button
                  type="button"
                  onClick={() => setIsCreatingSubject(!isCreatingSubject)}
                  className="inline-flex items-center px-3 py-2 border border-gray-300 shadow-sm text-sm leading-4 font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none"
                >
                  <Plus className="h-4 w-4" />
                </button>
              </div>
              
              {/* Inline Subject Creation */}
              {isCreatingSubject && (
                <div className="mt-2 flex gap-2 animate-in fade-in slide-in-from-top-1">
                  <input
                    type="text"
                    placeholder="New Subject Name"
                    className="shadow-sm focus:ring-vnoi-500 focus:border-vnoi-500 block w-full sm:text-sm border-gray-300 rounded-md p-2 border"
                    value={newSubjectName}
                    onChange={(e) => setNewSubjectName(e.target.value)}
                  />
                  <button
                    type="button"
                    onClick={handleCreateSubject}
                    className="inline-flex items-center px-3 py-2 border border-transparent text-sm leading-4 font-medium rounded-md text-white bg-green-600 hover:bg-green-700 shadow-sm"
                  >
                    Save
                  </button>
                </div>
              )}
            </div>

            {/* Tags */}
            <div className="sm:col-span-3">
              <label htmlFor="tags" className="block text-sm font-medium text-gray-700">
                Tags (comma separated)
              </label>
              <div className="mt-1">
                <input
                  type="text"
                  name="tags"
                  id="tags"
                  className="shadow-sm focus:ring-vnoi-500 focus:border-vnoi-500 block w-full sm:text-sm border-gray-300 rounded-md p-2 border"
                  placeholder="Calculus, Limits, Derivates"
                  value={formData.tags}
                  onChange={(e) => setFormData({ ...formData, tags: e.target.value })}
                />
              </div>
            </div>

            {/* Description */}
            <div className="sm:col-span-6">
              <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                Description
              </label>
              <div className="mt-1">
                <textarea
                  id="description"
                  name="description"
                  rows={6}
                  required
                  className="shadow-sm focus:ring-vnoi-500 focus:border-vnoi-500 block w-full sm:text-sm border border-gray-300 rounded-md p-2"
                  placeholder="Enter the problem statement..."
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                />
              </div>
            </div>

            {/* Solution */}
            <div className="sm:col-span-6">
              <label htmlFor="solution" className="block text-sm font-medium text-gray-700">
                Reference Solution / Proof
              </label>
              <div className="mt-1">
                <textarea
                  id="solution"
                  name="solution"
                  rows={6}
                  className="shadow-sm focus:ring-vnoi-500 focus:border-vnoi-500 block w-full sm:text-sm border border-gray-300 rounded-md p-2"
                  placeholder="Enter the official solution or answer key (used by AI for grading)..."
                  value={formData.solution}
                  onChange={(e) => setFormData({ ...formData, solution: e.target.value })}
                />
              </div>
              <p className="mt-2 text-sm text-gray-500">
                This solution will be used as a ground truth for the AI judge but won't be visible to users immediately.
              </p>
            </div>

          </div>

          <div className="pt-5 border-t border-gray-200">
            <div className="flex justify-end">
              <button
                type="button"
                onClick={() => navigate('/')}
                className="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-vnoi-500"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading}
                className="ml-3 inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-vnoi-700 hover:bg-vnoi-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-vnoi-500 disabled:opacity-50"
              >
                {loading ? 'Creating...' : 'Create Problem'}
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateProblem;