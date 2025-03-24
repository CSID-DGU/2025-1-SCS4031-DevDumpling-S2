import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/layout/Header';
import Home from './pages/Home';

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-100">
        <Header />
        <main>
          <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/community" element={<div>커뮤니티</div>} />
              <Route path="/challenge" element={<div>챌린지</div>} />
              <Route path="/profile" element={<div>프로필</div>} />
            </Routes>
          </div>
        </main>
      </div>
    </Router>
  );
}

export default App; 