// tailwind.config.js
module.exports = {
    content: [
        "./src/main/resources/templates/**/*.{html,js}",
        "./src/main/resources/static/**/*.{html,js}",
    ],
    theme: {
        extend: {
            // primary: indigo-600, secondary: indigo-400
            colors: { primary: "#4F46E5", secondary: "#818CF8" },
            borderRadius: {
                none: "0px",
                sm: "4px",
                DEFAULT: "8px",
                md: "12px",
                lg: "16px",
                xl: "20px",
                "2xl": "24px",
                "3xl": "32px",
                full: "9999px",
                button: "8px",
            },
        },
    },
    plugins: [],
};
