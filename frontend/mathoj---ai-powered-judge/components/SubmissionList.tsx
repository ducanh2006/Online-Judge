import React, { useEffect, useState } from 'react';
import { submissionService } from '../services/api';
import { SubmissionDTO } from '../types';
import { List, CheckCircle, Clock } from 'lucide-react';
import { Link } from 'react-router-dom';

const SubmissionList: React.FC = () => {
  const [submissions, setSubmissions] = useState<SubmissionDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    fetchSubmissions();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page]);

  const fetchSubmissions = async () => {
    setLoading(true);
    try {
      const data = await submissionService.getAll(page, 20);
      setSubmissions(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error("Failed to fetch submissions", error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
          <List className="text-vnoi-700" />
          Submission Status
        </h1>
      </div>

      <div className="bg-white rounded-lg shadow overflow-hidden border border-gray-200">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider w-20">ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Time</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">User ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Problem ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Score</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {loading ? (
                <tr>
                   <td colSpan={6} className="px-6 py-8 text-center text-gray-500">Loading history...</td>
                </tr>
              ) : submissions.length === 0 ? (
                <tr>
                   <td colSpan={6} className="px-6 py-8 text-center text-gray-500">No submissions yet.</td>
                </tr>
              ) : (
                submissions.map((sub) => (
                  <tr key={sub.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <a href="#" className="hover:underline text-vnoi-700">#{sub.id}</a>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {formatDate(sub.submittedAt)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-medium">
                        {sub.userId}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-blue-600">
                        <Link to={`/problem/${sub.problemId}`} className="hover:underline">
                            Problem {sub.problemId}
                        </Link>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {sub.status === 'Completed' ? (
                        <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                          <CheckCircle className="w-3 h-3" /> Completed
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                          <Clock className="w-3 h-3" /> Pending
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-bold text-gray-900">
                        {sub.score !== undefined ? sub.score : '-'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        
        {/* Pagination controls (simplified) */}
        <div className="bg-gray-50 px-4 py-3 flex items-center justify-end border-t border-gray-200 sm:px-6 gap-2">
            <button 
                disabled={page === 1} 
                onClick={() => setPage(p => p - 1)}
                className="px-3 py-1 border rounded bg-white disabled:opacity-50 text-sm"
            >
                Prev
            </button>
            <span className="text-sm text-gray-600">Page {page} of {totalPages}</span>
            <button 
                disabled={page >= totalPages} 
                onClick={() => setPage(p => p + 1)}
                className="px-3 py-1 border rounded bg-white disabled:opacity-50 text-sm"
            >
                Next
            </button>
        </div>
      </div>
    </div>
  );
};

export default SubmissionList;
