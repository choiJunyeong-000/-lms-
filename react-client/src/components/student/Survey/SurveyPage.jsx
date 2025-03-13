import React, { useState } from 'react';

import SurveyForm from './GeneralSurvey';
import LectureSurvey from './LectureSurvey';
import styles from './SurveyPage.module.css'; // CSS Modules import

function SurveyPage() {
  const [activeTab, setActiveTab] = useState('lecture');

  return (
    <div className={styles.dashboard}>
      <main className={styles.mainContent}>
        {/* 탭 버튼 영역 */}
        <div className={styles.tabButtons}>
          <button
            className={`${styles.tabButton} ${activeTab === 'lecture' ? styles.activeTab : ''}`}
            onClick={() => setActiveTab('lecture')}
          >
            강의 평가
          </button>
          
          <button
            className={`${styles.tabButton} ${activeTab === 'survey' ? styles.activeTab : ''}`}
            onClick={() => setActiveTab('survey')}
          >
            일반 설문
          </button>
        </div>

        {/* 탭에 따른 컴포넌트 렌더링 */}
        {activeTab === 'lecture' && (
          <div className={styles.surveyContainer}>
            <LectureSurvey />
          </div>
        )}
       
        {activeTab === 'survey' && (
          <div className={styles.surveyContainer}>
            {/* onComplete 콜백 호출 시 activeTab을 초기화하여 SurveyForm을 언마운트 */}
            <SurveyForm onComplete={() => setActiveTab('')} />
          </div>
        )}
      </main>
    </div>
  );
}

export default SurveyPage;
