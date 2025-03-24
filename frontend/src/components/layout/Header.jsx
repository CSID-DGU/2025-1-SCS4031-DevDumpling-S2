import React from 'react';
import { Link } from 'react-router-dom';

const Header = () => {
  return (
    <header className="bg-white shadow">
      <nav className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">
          <div className="flex">
            <Link to="/" className="flex-shrink-0 flex items-center">
              <span className="text-2xl font-bold text-primary">DevDumpling</span>
            </Link>
            <div className="hidden sm:ml-6 sm:flex sm:space-x-8">
              <Link to="/community" className="text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 border-transparent hover:border-primary">
                커뮤니티
              </Link>
              <Link to="/challenge" className="text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 border-transparent hover:border-primary">
                챌린지
              </Link>
              <Link to="/profile" className="text-gray-900 inline-flex items-center px-1 pt-1 border-b-2 border-transparent hover:border-primary">
                프로필
              </Link>
            </div>
          </div>
          <div className="flex items-center">
            <button className="bg-primary text-white px-4 py-2 rounded-md hover:bg-primary/90">
              로그인
            </button>
          </div>
        </div>
      </nav>
    </header>
  );
};

export default Header; 