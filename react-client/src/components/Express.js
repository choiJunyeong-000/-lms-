const express = require('express');
const cors = require('cors');
const app = express();

const corsOptions = {
  origin: 'http://localhost:3000', // 허용할 도메인
  optionsSuccessStatus: 200
};

app.use(cors(corsOptions));

app.get('/metrics-view', (req, res) => {
  // 모니터링 데이터를 반환하는 로직
  res.json({ /* 모니터링 데이터 */ });
});

app.listen(8090, () => {
 
});