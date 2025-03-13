import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Bar, Line } from 'react-chartjs-2';
import 'chart.js/auto';
import './MonitoringPage.css';

function MonitoringPage() {
  const [metrics, setMetrics] = useState(null);
  const [networkStats, setNetworkStats] = useState({ rx: 0, tx: 0 });
  const [lastNetworkData, setLastNetworkData] = useState(null);

  // 📌 API 호출 함수 (JWT 포함)
  const fetchMetrics = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        console.error("❌ JWT 토큰이 없습니다.");
        return;
      }

    
      const response = await axios.get("http://localhost:8090/metrics-view", {
        headers: { Authorization: `Bearer ${token}` }
      });

  
      setMetrics(response.data);
      
      // 네트워크 변화량 계산
      if (lastNetworkData && response.data.network) {
        const currentTime = new Date().getTime();
        const timeDiff = (currentTime - lastNetworkData.timestamp) / 1000; // 초 단위로 변환
        
        let totalRx = 0;
        let totalTx = 0;
        
        // 모든 네트워크 인터페이스의 데이터 합산 (lo 제외)
        response.data.network.forEach((currentNet, idx) => {
          if (currentNet.interface_name !== 'lo' && lastNetworkData.data[idx]) {
            const prevNet = lastNetworkData.data.find(net => net.interface_name === currentNet.interface_name);
            
            if (prevNet) {
              // 'tx'와 'rx' 필드가 이미 KB/s 단위인지 확인
              // 그렇지 않다면 누적 데이터를 이용해 계산
              if (typeof currentNet.tx === 'number' && typeof prevNet.tx === 'number') {
                totalTx += (currentNet.tx - prevNet.tx) / timeDiff;
              } else if (typeof currentNet.cumulative_tx === 'number' && typeof prevNet.cumulative_tx === 'number') {
                totalTx += (currentNet.cumulative_tx - prevNet.cumulative_tx) / 1024 / timeDiff;
              }
              
              if (typeof currentNet.rx === 'number' && typeof prevNet.rx === 'number') {
                totalRx += (currentNet.rx - prevNet.rx) / timeDiff;
              } else if (typeof currentNet.cumulative_rx === 'number' && typeof prevNet.cumulative_rx === 'number') {
                totalRx += (currentNet.cumulative_rx - prevNet.cumulative_rx) / 1024 / timeDiff;
              }
            }
          }
        });
        
        setNetworkStats({
          rx: totalRx,
          tx: totalTx
        });
      }
      
      // 마지막 네트워크 데이터 저장
      if (response.data.network) {
        setLastNetworkData({
          data: response.data.network,
          timestamp: new Date().getTime()
        });
      }
    } catch (error) {
      console.error("❌ API 호출 오류:", error);
    }
  };

  // 📌 2초마다 데이터 갱신
  useEffect(() => {
    fetchMetrics();
    const interval = setInterval(fetchMetrics, 2000);
    return () => clearInterval(interval);
  }, []);

  // 📌 차트 데이터 준비
  const cpuData = metrics ? {
    labels: metrics.percpu.map((cpu) => `CPU ${cpu.cpu_number}`),
    datasets: [
      {
        label: '유휴',
        data: metrics.percpu.map(cpu => cpu.idle),
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1,
      },
      {
        label: '시스템',
        data: metrics.percpu.map(cpu => cpu.system),
        backgroundColor: 'rgba(255, 99, 132, 0.2)',
        borderColor: 'rgba(255, 99, 132, 1)',
        borderWidth: 1,
      },
      {
        label: '사용자',
        data: metrics.percpu.map(cpu => cpu.user),
        backgroundColor: 'rgba(54, 162, 235, 0.2)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 1,
      },
      {
        label: '총 사용률',
        data: metrics.percpu.map(cpu => cpu.total),
        backgroundColor: 'rgba(153, 102, 255, 0.2)',
        borderColor: 'rgba(153, 102, 255, 1)',
        borderWidth: 1,
      },
    ],
  } : {};

  const processData = metrics ? {
    labels: ['실행 중', '총 프로세스', '스레드'],
    datasets: [{
      label: '프로세스 카운트',
      data: [metrics.processcount.running, metrics.processcount.total, metrics.processcount.thread],
      backgroundColor: 'rgba(255, 206, 86, 0.2)',
      borderColor: 'rgba(255, 206, 86, 1)',
      borderWidth: 1,
    }],
  } : {};

  const networkData = metrics ? {
    labels: metrics.network.map(net => net.interface_name || net.interface),
    datasets: [
      {
        label: '수신 바이트',
        data: metrics.network.map(net => net.cumulative_rx || net.rx_bytes),
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1,
      },
      {
        label: '송신 바이트',
        data: metrics.network.map(net => net.cumulative_tx || net.tx_bytes),
        backgroundColor: 'rgba(255, 99, 132, 0.2)',
        borderColor: 'rgba(255, 99, 132, 1)',
        borderWidth: 1,
      },
    ],
  } : {};

  // 활성 사용자 수를 추정하거나 계산하는 함수
  const getActiveUsers = () => {
    if (metrics && metrics.connections && metrics.connections.tcp_count) {
      return metrics.connections.tcp_count;
    }

    return "데이터 없음"; // 실제 데이터 없음을 명시
  };

  const activeUsers = getActiveUsers();

  return (
    <div className="monitoring-container">
      <h1>📊 시스템 모니터링</h1>

      {metrics ? (
        <div>
          {/* 프로세스 카운트 */}
          <h3>⚙️ 프로세스 카운트</h3>
          <div className="chart-container">
            <Bar data={processData} options={{ responsive: true }} />
          </div>
          <table className="monitoring-table">
            <tbody>
              <tr><th>실행 중</th><td>{metrics.processcount.running}</td></tr>
              <tr><th>총 프로세스</th><td>{metrics.processcount.total}</td></tr>
              <tr><th>스레드</th><td>{metrics.processcount.thread}</td></tr>
            </tbody>
          </table>

          {/* CPU 사용률 */}
          <h3>💻 CPU 사용률</h3>
          <div className="chart-container">
            <Line data={cpuData} options={{ responsive: true }} />
          </div>
          <table className="monitoring-table">
            <thead>
              <tr><th>CPU 번호</th><th>유휴</th><th>시스템</th><th>사용자</th><th>총 사용률</th></tr>
            </thead>
            <tbody>
              {metrics.percpu.map((cpu, index) => (
                <tr key={index}>
                  <td>CPU {cpu.cpu_number}</td>
                  <td>{cpu.idle}%</td>
                  <td>{cpu.system}%</td>
                  <td>{cpu.user}%</td>
                  <td>{cpu.total}%</td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* 네트워크 사용량 */}
<h3>📡 네트워크 사용량</h3>
<div className="chart-container">
  <Bar data={networkData} options={{ responsive: true }} />
</div>
<table className="monitoring-table">
  <thead>
    <tr><th>인터페이스</th><th>수신 바이트</th><th>송신 바이트</th></tr>
  </thead>
  <tbody>
    {metrics.network.map((net, index) => (
      <tr key={index}>
        <td>
          {net.interface_name === 'lo' ? '루프백' : 
           net.interface_name === 'eth0' ? '유선네트워크' : 
           net.interface_name || net.interface}
        </td>
        <td>{net.cumulative_rx || net.rx_bytes}</td>
        <td>{net.cumulative_tx || net.tx_bytes}</td>
      </tr>
    ))}
  </tbody>
</table>

        </div>
      ) : (
        <p>⏳ 로딩 중...</p>
      )}
    </div>
  );
}

export default MonitoringPage;
