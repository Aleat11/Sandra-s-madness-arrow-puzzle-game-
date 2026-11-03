import type { Config } from "tailwindcss";

const config: Config = {
  darkMode: "class",
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}"
  ],
  theme: {
    extend: {
      colors: {
        sandra: {
          terracotta: "#D9532F",
          cream: "#FFFDF9",
          linen: "#FFF8F0",
          espresso: "#2D1B14"
        },
        naty: {
          teal: "#0EA5E9",
          brioche: "#F59E0B"
        },
        ally: {
          berry: "#EC4899",
          lettuce: "#16A34A"
        }
      }
    }
  },
  plugins: []
};

export default config;
