import React from 'react';

const Home = () => {
  return (
    <div className="space-y-8">
      <section className="text-center py-12">
        <h2 className="text-4xl font-bold text-gray-900 mb-4">
          경제 지식을 공유하고 성장하세요
        </h2>
        <p className="text-xl text-gray-600">
          DevDumpling과 함께 경제 지식을 쌓고, 커뮤니티에서 다양한 의견을 나누세요
        </p>
      </section>

      <section className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-lg shadow">
          <h3 className="text-xl font-semibold mb-2">커뮤니티</h3>
          <p className="text-gray-600">다양한 경제 주제에 대해 토론하고 의견을 나누세요</p>
        </div>
        <div className="bg-white p-6 rounded-lg shadow">
          <h3 className="text-xl font-semibold mb-2">챌린지</h3>
          <p className="text-gray-600">함께 목표를 설정하고 달성해보세요</p>
        </div>
        <div className="bg-white p-6 rounded-lg shadow">
          <h3 className="text-xl font-semibold mb-2">AI 추천</h3>
          <p className="text-gray-600">맞춤형 경제 정보와 학습 자료를 받아보세요</p>
        </div>
      </section>
    </div>
  );
};

export default Home; 