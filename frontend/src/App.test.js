import { render, screen } from '@testing-library/react';
import App from './App';

test('renders main heading', () => {
  render(<App />);
  const headingElement = screen.getByText(/Jweed Frontend/i);
  expect(headingElement).toBeInTheDocument();
});
