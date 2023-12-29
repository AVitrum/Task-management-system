
/** @type {import('tailwindcss').Config} */
export default {
  content: [ 
    "./src/**/*.{html,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Nunito Sans', 'sans-serif'], // Замініть шрифт на потрібний вам
      },
      
      colors:{
        primary: '#2022225',
        secondary: '#5865f2',
      },
      
    },
  },
  plugins: [],
}

