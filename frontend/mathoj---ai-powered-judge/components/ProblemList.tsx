
import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { problemService, subjectService, tagService } from '../services/api';
import { ProblemDTO, SubjectDTO, TagDTO, ProblemSearchParams } from '../types';
import { BookOpen, Tag as TagIcon, ChevronLeft, ChevronRight, Search, Filter, X } from 'lucide-react';

const ProblemList: React.FC = () => {
  const [problems, setProblems] = useState<ProblemDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(1);
  
  // Metadata state for filters
  const [subjects, setSubjects] = useState<SubjectDTO[]>([]);
  const [tags, setTags] = useState<TagDTO[]>([]);

  // Search Param State
  const [searchParams, setSearchParams] = useState<ProblemSearchParams>({
    page: 1,
    size: 20,
    sort: 'lastUpdated,desc',
    subject: [],
    tag: []
  });

  // Temporary filter state for the UI (applied when user clicks "Search")
  const [filterId, setFilterId] = useState<string>('');
  const [filterSubjectId, setFilterSubjectId] = useState<string>('');
  const [filterTags, setFilterTags] = useState<number[]>([]);

  useEffect(() => {
    // Load metadata (Subjects/Tags) once
    const fetchMetadata = async () => {
        try {
            const [subjData, tagData] = await Promise.all([
                subjectService.getAll(),
                tagService.getAll()
            ]);
            setSubjects(subjData);
            setTags(tagData);
        } catch (e) {
            console.error("Failed to load metadata filters", e);
        }
    };
    fetchMetadata();
  }, []);

  useEffect(() => {
    fetchProblems();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchParams]);

  const fetchProblems = async () => {
    setLoading(true);
    try {
      const data = await problemService.getAll(searchParams);
      setProblems(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error("Failed to fetch problems", error);
      setProblems([]);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (newPage: number) => {
    setSearchParams(prev => ({ ...prev, page: newPage }));
  };

  const handleApplyFilters = () => {
      setSearchParams(prev => ({
          ...prev,
          page: 1, // Reset to first page on new search
          id: filterId ? parseInt(filterId) : undefined,
          subject: filterSubjectId ? [parseInt(filterSubjectId)] : [],
          tag: filterTags.length > 0 ? filterTags : []
      }));
  };

  const handleClearFilters = () => {
      setFilterId('');
      setFilterSubjectId('');
      setFilterTags([]);
      setSearchParams({
          page: 1,
          size: 20,
          sort: 'lastUpdated,desc',
          subject: [],
          tag: []
      });
  };

  const toggleTagFilter = (tagId: number) => {
      setFilterTags(prev => 
        prev.includes(tagId) 
            ? prev.filter(id => id !== tagId)
            : [...prev, tagId]
      );
  };

  const getDifficultyColor = (diff: number) => {
      if (diff >= 8) return 'bg-red-100 text-red-800';
      if (diff >= 4) return 'bg-yellow-100 text-yellow-800';
      return 'bg-green-100 text-green-800';
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
          <BookOpen className="text-vnoi-700" />
          Problem Set
        </h1>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          
          {/* Main Content: Problem Table */}
          <div className="lg:col-span-3">
              <div className="bg-white rounded-lg shadow overflow-hidden border border-gray-200">
                <div className="overflow-x-auto">
                  <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                      <tr>
                        <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-20">ID</th>
                        <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
                        <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-32">Difficulty</th>
                        <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-48">Subject</th>
                        <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tags</th>
                      </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                      {loading ? (
                        <tr>
                          <td colSpan={5} className="px-6 py-12 text-center text-gray-500">
                              <div className="flex justify-center items-center gap-2">
                                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-vnoi-700"></div>
                                  Loading problems...
                              </div>
                          </td>
                        </tr>
                      ) : problems.length === 0 ? (
                        <tr>
                          <td colSpan={5} className="px-6 py-12 text-center text-gray-500">
                              No problems found matching your criteria.
                          </td>
                        </tr>
                      ) : (
                        problems.map((prob) => (
                          <tr key={prob.id} className="hover:bg-gray-50 transition-colors">
                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-vnoi-700">
                              #{prob.id}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-blue-600">
                              <Link to={`/problem/${prob.id}`} className="hover:underline">
                                {prob.title}
                              </Link>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                              <span className={`px-2 py-1 rounded text-xs font-semibold ${getDifficultyColor(prob.difficulty)}`}>
                                Level {prob.difficulty}
                              </span>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                              {prob.subject?.name || 'General'}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                              <div className="flex flex-wrap gap-1">
                                {prob.tags && prob.tags.map(tag => (
                                  <span key={tag.id} className="inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-gray-100 text-gray-800">
                                    <TagIcon className="w-3 h-3 mr-1" />
                                    {tag.name}
                                  </span>
                                ))}
                              </div>
                            </td>
                          </tr>
                        ))
                      )}
                    </tbody>
                  </table>
                </div>

                {/* Pagination */}
                <div className="bg-white px-4 py-3 flex items-center justify-between border-t border-gray-200 sm:px-6">
                    <div className="flex-1 flex justify-between sm:hidden">
                      <button 
                        disabled={(searchParams.page || 1) === 1} 
                        onClick={() => handlePageChange((searchParams.page || 1) - 1)} 
                        className="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
                      >
                        Previous
                      </button>
                      <button 
                        disabled={(searchParams.page || 1) >= totalPages} 
                        onClick={() => handlePageChange((searchParams.page || 1) + 1)} 
                        className="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
                      >
                        Next
                      </button>
                    </div>
                    <div className="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
                      <div>
                        <p className="text-sm text-gray-700">
                          Page <span className="font-medium">{searchParams.page || 1}</span> of <span className="font-medium">{totalPages}</span>
                        </p>
                      </div>
                      <div>
                        <nav className="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
                          <button
                            onClick={() => handlePageChange((searchParams.page || 1) - 1)}
                            disabled={(searchParams.page || 1) === 1}
                            className="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50"
                          >
                            <span className="sr-only">Previous</span>
                            <ChevronLeft className="h-5 w-5" />
                          </button>
                          <button
                            onClick={() => handlePageChange((searchParams.page || 1) + 1)}
                            disabled={(searchParams.page || 1) >= totalPages}
                            className="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50"
                          >
                            <span className="sr-only">Next</span>
                            <ChevronRight className="h-5 w-5" />
                          </button>
                        </nav>
                      </div>
                    </div>
                </div>
              </div>
          </div>

          {/* Sidebar: Filters */}
          <div className="lg:col-span-1">
             <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-5 sticky top-24">
                 <div className="flex items-center justify-between mb-4 border-b border-gray-100 pb-2">
                    <h2 className="text-lg font-bold text-gray-900 flex items-center gap-2">
                        <Filter className="w-5 h-5 text-gray-500" />
                        Filters
                    </h2>
                    {(filterId || filterSubjectId || filterTags.length > 0) && (
                        <button onClick={handleClearFilters} className="text-xs text-red-600 hover:text-red-800 flex items-center gap-1">
                            <X className="w-3 h-3" /> Clear
                        </button>
                    )}
                 </div>

                 <div className="space-y-4">
                     {/* Search by ID */}
                     <div>
                         <label className="block text-sm font-medium text-gray-700 mb-1">Problem ID</label>
                         <div className="relative">
                             <input 
                                type="number" 
                                placeholder="e.g. 10" 
                                className="w-full pl-9 pr-3 py-2 border border-gray-300 rounded-md shadow-sm focus:ring-vnoi-500 focus:border-vnoi-500 text-sm"
                                value={filterId}
                                onChange={(e) => setFilterId(e.target.value)}
                             />
                             <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                <Search className="h-4 w-4 text-gray-400" />
                             </div>
                         </div>
                     </div>

                     {/* Subject Filter */}
                     <div>
                         <label className="block text-sm font-medium text-gray-700 mb-1">Subject</label>
                         <select 
                            className="w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:ring-vnoi-500 focus:border-vnoi-500 text-sm"
                            value={filterSubjectId}
                            onChange={(e) => setFilterSubjectId(e.target.value)}
                         >
                             <option value="">All Subjects</option>
                             {subjects.map(s => (
                                 <option key={s.id} value={s.id}>{s.name}</option>
                             ))}
                         </select>
                     </div>

                     {/* Tags Filter */}
                     <div>
                         <label className="block text-sm font-medium text-gray-700 mb-2">Tags</label>
                         <div className="max-h-48 overflow-y-auto border border-gray-200 rounded-md p-2 bg-gray-50">
                             {tags.length === 0 ? (
                                 <p className="text-xs text-gray-400 text-center">No tags available</p>
                             ) : (
                                 <div className="flex flex-wrap gap-2">
                                     {tags.map(tag => (
                                         <button
                                            key={tag.id}
                                            onClick={() => toggleTagFilter(tag.id)}
                                            className={`inline-flex items-center px-2 py-1 rounded text-xs font-medium border transition-colors ${
                                                filterTags.includes(tag.id)
                                                  ? 'bg-vnoi-100 text-vnoi-700 border-vnoi-200'
                                                  : 'bg-white text-gray-600 border-gray-200 hover:border-gray-300'
                                            }`}
                                         >
                                             {tag.name}
                                         </button>
                                     ))}
                                 </div>
                             )}
                         </div>
                     </div>

                     <button 
                        onClick={handleApplyFilters}
                        className="w-full flex justify-center items-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-vnoi-700 hover:bg-vnoi-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-vnoi-500"
                     >
                         Apply Filters
                     </button>
                 </div>
             </div>
          </div>
      </div>
    </div>
  );
};

export default ProblemList;