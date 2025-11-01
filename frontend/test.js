import express from 'express';
import cors from 'cors';

const app = express();
app.use(cors());
app.use(express.json());

// Bộ nhớ tạm lưu emitter SSE cho từng submission
const clients = new Map();

// Fake DB
let submissionIdCounter = 1;
const submissions = {}; // { id: { status, result } }

// 🔹 API 1: Gửi bài (POST /api/submission)
app.post('/api/submission', (req, res) => {
  const { yourSolution } = req.body;
  const id = submissionIdCounter++;
  submissions[id] = { status: 'Pending', result: null };

  console.log(`[+] Submission ${id} nhận thành công`);

  // Giả lập xử lý lâu
  setTimeout( () => {
    console.log(`[~] Đang xử lý submission ${id}...`);

    // Giả lập gọi Gemini API (ở đây chỉ fake)
    const fakeAnswer = `Kết quả của bạn là: ${yourSolution.toUpperCase()}!`;

    submissions[id].status = 'Done';
    submissions[id].result = fakeAnswer;

    // Khi xong -> gửi qua SSE nếu client đang lắng nghe
    const client = clients.get(id);
    if (client) {
      client.res.write(`event: result\n`);
      client.res.write(`data: ${JSON.stringify(fakeAnswer)}\n\n`);
      client.res.end();
      clients.delete(id);
      console.log(`[✔] Đã gửi kết quả submission ${id}`);
    }
  }, 5000); // Giả lập xử lý 5 giây

  res.status(202).json({ id, status: 'Pending' });
});

// 🔹 API 2: Client mở stream SSE (GET /api/stream/:id)
app.get('/api/stream/:id', (req, res) => {
  const { id } = req.params;
  console.log(`[SSE] Client lắng nghe submission ${id}`);

  // Thiết lập header SSE
  res.setHeader('Content-Type', 'text/event-stream');
  res.setHeader('Cache-Control', 'no-cache');
  res.setHeader('Connection', 'keep-alive');

  // Lưu kết nối lại để gửi sau
  clients.set(Number(id), { res });

  // Khi client đóng kết nối
  req.on('close', () => {
    clients.delete(Number(id));
    console.log(`[SSE] Client ngắt kết nối submission ${id}`);
  });
});

app.listen(3000, () => {
  console.log('🚀 Server chạy tại http://localhost:3000');
});


// khi front end mở sse thì nó sẽ có đi kèm với id,
// khi này backend sẽ lưu response vào map cùng với id và không làm gì thêm.
//  sau đó trong callback settimeout 5 giây
//   thì khi nào hết 5 giây đoạn code fake gọi api bắt đầu được thực hiện và
//    khi này response đã được lưu trong map và khi này lấy reponse theo id và reponse 
//    và write và end