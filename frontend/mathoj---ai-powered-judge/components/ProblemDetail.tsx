
import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { problemService, submissionService } from '../services/api';
import { ProblemDTO } from '../types';
import { Send, Clock, Terminal, HelpCircle, Loader, Play } from 'lucide-react';

const ProblemDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [problem, setProblem] = useState<ProblemDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [solution, setSolution] = useState('');
  
  // Submission State
  const [submissionId, setSubmissionId] = useState<number | null>(null);
  const [outputLog, setOutputLog] = useState<string>('');
  // Status: idle -> submitting -> grading -> grading_complete -> explaining -> finished -> error
  const [status, setStatus] = useState<'idle' | 'submitting' | 'grading' | 'grading_complete' | 'explaining' | 'finished' | 'error'>('idle');
  
  const terminalEndRef = useRef<HTMLDivElement>(null);
  const eventSourceRef = useRef<EventSource | null>(null);

  useEffect(() => {
    if (id) {
      fetchProblem(parseInt(id));
    }
    // Cleanup SSE on unmount
    return () => {
        closeEventSource();
    };
  }, [id]);

  // Auto-scroll terminal
  useEffect(() => {
    terminalEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [outputLog]);

  const fetchProblem = async (problemId: number) => {
    try {
      const data = await problemService.getById(problemId);
      setProblem(data);
    } catch (error) {
      console.error("Error fetching problem", error);
    } finally {
      setLoading(false);
    }
  };

  const closeEventSource = () => {
      if (eventSourceRef.current) {
          eventSourceRef.current.close();
          eventSourceRef.current = null;
      }
  };

  const connectStream = (subId: number, type: number) => {
    const token = localStorage.getItem('token');
    if (!token) return;

    // Ensure any existing connection is closed
    closeEventSource();

    const url = submissionService.getStreamUrl(subId, type, token);
    const eventSource = new EventSource(url);
    eventSourceRef.current = eventSource;

    if (type === 1) {
        setStatus('grading');
        setOutputLog(prev => prev + "⏳ Đang kết nối tới server chấm điểm...\n");
    } else {
        setStatus('explaining');
        setOutputLog(prev => prev + "\n⏳ Đang lấy giải thích từ AI...\n");
    }

    // 1. Listen for standard messages (chunks of data)
    eventSource.onmessage = (event) => {
        setOutputLog(prev => prev + event.data);
    };

    // 2. Listen for custom 'complete' event (end of stream)
    eventSource.addEventListener('complete', () => {
        if (type === 1) {
            setOutputLog(prev => prev + "\n\n🎉 Hoàn thành chấm điểm!");
            setStatus('grading_complete');
        } else {
            setOutputLog(prev => prev + "\n\n✅ Hoàn tất giải thích!");
            setStatus('finished');
        }
        closeEventSource();
    });

    eventSource.onerror = (e) => {
        if (eventSource.readyState === EventSource.CLOSED) return;
        
        console.error("SSE Error", e);
        setOutputLog(prev => prev + "\n❌ Lỗi kết nối hoặc server ngắt kết nối.");
        setStatus('error');
        closeEventSource();
    };
  };

  const handleSubmit = async () => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }

    if (!solution.trim()) return;
    if (!problem) return;

    setStatus('submitting');
    setOutputLog('');
    setSubmissionId(null);

    try {
      // Step 1: Create Submission
      outputLog && setOutputLog(''); // Clear previous log
      
      const response = await submissionService.create({
        problemId: problem.id,
        yourSolution: solution
      });
      
      setSubmissionId(response.submissionId);
      setOutputLog(prev => prev + `✅ Submission #${response.submissionId} tạo thành công.\nĐang bắt đầu chấm...\n\n`);
      
      // Step 2: Start Grading Stream (Type 1)
      connectStream(response.submissionId, 1);

    } catch (error) {
      console.error("Submission failed", error);
      setStatus('error');
      setOutputLog(prev => prev + "❌ Lỗi: Không thể gửi bài.\n");
    }
  };

  const handleRequestExplanation = () => {
      if (submissionId) {
          // Step 3: Start Explanation Stream (Type 2)
          connectStream(submissionId, 2);
      }
  };

  if (loading) return <div className="p-8 text-center text-gray-500">Loading problem...</div>;
  if (!problem) return <div className="p-8 text-center text-gray-500">Problem not found.</div>;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Left Column: Problem Info */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div className="flex justify-between items-start border-b border-gray-100 pb-4 mb-4">
              <div>
                 <h1 className="text-3xl font-bold text-gray-900 mb-2">{problem.title}</h1>
                 <div className="flex items-center gap-4 text-sm text-gray-500">
                    <span className="flex items-center gap-1">
                       <Clock className="w-4 h-4" /> 1s limit
                    </span>
                    <span className="bg-gray-100 text-gray-700 px-2 py-0.5 rounded">
                      {problem.subject?.name}
                    </span>
                    <span className={`${
                        (problem.difficulty >= 8) ? 'text-red-600' : 
                        (problem.difficulty >= 4) ? 'text-yellow-600' : 'text-green-600'
                    } font-medium`}>
                        Level {problem.difficulty}
                    </span>
                 </div>
              </div>
              <div className="text-4xl font-mono text-gray-200">#{problem.id}</div>
            </div>

            <div className="prose max-w-none text-gray-800">
              <h3 className="text-lg font-semibold text-gray-900 mb-2">Description</h3>
              <div className="whitespace-pre-wrap font-serif text-lg leading-relaxed">
                {problem.description}
              </div>
            </div>
            
            <div className="mt-6 pt-4 border-t border-gray-100">
                <h4 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-2">Tags</h4>
                <div className="flex flex-wrap gap-2">
                    {problem.tags.map(tag => (
                        <span key={tag.id} className="bg-blue-50 text-blue-700 px-2 py-1 rounded text-xs font-medium">
                            {tag.name}
                        </span>
                    ))}
                </div>
            </div>
          </div>
        </div>

        {/* Right Column: Submission & Terminal */}
        <div className="lg:col-span-1 space-y-6">
          
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 sticky top-24">
            <h2 className="text-lg font-bold text-gray-900 mb-4 flex items-center gap-2">
                <Send className="w-5 h-5 text-vnoi-700" />
                Submit Solution
            </h2>
            
            <div className="mb-4">
              <label htmlFor="solution" className="block text-sm font-medium text-gray-700 mb-1">
                Your Answer / Proof
              </label>
              <textarea
                id="solution"
                rows={8}
                className="w-full rounded-md border-gray-300 shadow-sm focus:border-vnoi-500 focus:ring-vnoi-500 sm:text-sm p-3 border resize-none font-mono"
                placeholder="Dán code hoặc lời giải vào đây..."
                value={solution}
                onChange={(e) => setSolution(e.target.value)}
                disabled={status === 'grading' || status === 'submitting' || status === 'explaining'}
              ></textarea>
            </div>

            <button
              onClick={handleSubmit}
              disabled={status === 'grading' || status === 'submitting' || !solution.trim() || status === 'explaining'}
              className="w-full flex justify-center items-center gap-2 py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-vnoi-700 hover:bg-vnoi-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-vnoi-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {status === 'submitting' ? (
                  <>Đang gửi bài...</> 
              ) : status === 'grading' ? (
                  <><Loader className="w-4 h-4 animate-spin"/> Đang chấm...</>
              ) : (
                  <><Play className="w-4 h-4"/> Gửi bài và chấm điểm</>
              )}
            </button>
          </div>

          {(status !== 'idle' || outputLog) && (
             <div className="bg-gray-900 rounded-lg shadow-lg overflow-hidden border border-gray-800">
                <div className="bg-gray-800 px-4 py-2 flex items-center justify-between border-b border-gray-700">
                   <div className="flex items-center gap-2 text-gray-300 text-sm font-mono">
                      <Terminal className="w-4 h-4" />
                      Kết quả
                   </div>
                   <div className="flex items-center gap-2">
                       {status === 'grading' && <span className="text-xs text-blue-300 animate-pulse">● Live Grading</span>}
                       {status === 'explaining' && <span className="text-xs text-purple-300 animate-pulse">● Explaining</span>}
                   </div>
                </div>
                
                <div className="p-4 h-80 overflow-y-auto font-mono text-sm text-green-400 whitespace-pre-wrap leading-tight bg-black">
                   {outputLog}
                   <div ref={terminalEndRef} />
                </div>

                {/* Show Explanation Button only after grading completes */}
                {status === 'grading_complete' && (
                    <div className="bg-gray-800 px-4 py-3 border-t border-gray-700 flex flex-col sm:flex-row items-center justify-between gap-3 animate-in slide-in-from-bottom-2">
                        <span className="text-sm text-gray-300">Bạn có muốn xem giải thích?</span>
                        <button 
                            onClick={handleRequestExplanation}
                            className="w-full sm:w-auto flex items-center justify-center gap-2 px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white text-sm font-medium rounded transition-colors shadow-lg"
                        >
                            <HelpCircle className="w-4 h-4" />
                            Giải thích kết quả
                        </button>
                    </div>
                )}
             </div>
          )}

        </div>
      </div>
    </div>
  );
};

export default ProblemDetail;