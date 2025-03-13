import { render, screen } from '@testing-library/react';
import App from './App';

test('renders admin login form when not logged in', () => {
  render(<App />);
  const loginElement = screen.getByText(/로그인/i);
  expect(loginElement).toBeInTheDocument();
});

test('renders user registration form when logged in', () => {
  // Mock the login state
  render(<App isLoggedIn={true} />);
  const userFormElement = screen.getByText(/회원 추가/i);
  expect(userFormElement).toBeInTheDocument();
});