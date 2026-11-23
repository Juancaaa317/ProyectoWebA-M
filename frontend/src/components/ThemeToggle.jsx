import { Moon, Sun } from "lucide-react";
import { useTheme } from "../context/ThemeContext";
import "../styles/themeToggle.css";

function ThemeToggle() {
  const { theme, toggleTheme } = useTheme();
  const isDark = theme === "dark";

  return (
    <button
      type="button"
      className={isDark ? "theme-toggle is-dark" : "theme-toggle"}
      onClick={toggleTheme}
      aria-label={isDark ? "Cambiar a modo claro" : "Cambiar a modo oscuro"}
      title={isDark ? "Modo claro" : "Modo oscuro"}
    >
      <span className="theme-toggle-icons" aria-hidden="true">
        <Sun className="theme-toggle-sun" size={20} strokeWidth={1.8} />
        <Moon className="theme-toggle-moon" size={20} strokeWidth={1.8} />
      </span>
    </button>
  );
}

export default ThemeToggle;
