import "../styles/transiciones.css";

function PageLayout({ children }) {
    return (
        <div className="page-transition-enter-active">{children}</div>
    );
}

export default PageLayout;
