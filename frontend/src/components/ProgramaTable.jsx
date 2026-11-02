function ProgramaTable({ programas, onEditar, onEliminar, disabled }) {
    if (programas.length === 0) {
        return (
            <p className="admin-vacio">
                Aún no hay programas. Crea el primero con el botón "Nuevo programa".
            </p>
        );
    }

    return (
        <div className="admin-tabla-wrapper">
            <table className="admin-tabla">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Descripción</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {programas.map((p) => {
                        const activo = p.estadoPrograma === "ACTIVO";

                        return (
                            <tr key={p.idPrograma}>
                                <td>{p.idPrograma}</td>
                                <td>{p.nombrePrograma}</td>
                                <td>{p.descripcion || "—"}</td>
                                <td>
                                    <span
                                        className={
                                            activo ? "admin-badge activo" : "admin-badge inactivo"
                                        }
                                    >
                                        {activo ? "Activo" : "Inactivo"}
                                    </span>
                                </td>
                                <td className="admin-acciones">
                                    <button
                                        type="button"
                                        className="admin-btn-editar"
                                        onClick={() => onEditar(p)}
                                        disabled={disabled}
                                    >
                                        Editar
                                    </button>
                                    <button
                                        type="button"
                                        className="admin-btn-eliminar"
                                        onClick={() => onEliminar(p)}
                                        disabled={disabled}
                                    >
                                        Eliminar
                                    </button>
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        </div>
    );
}

export default ProgramaTable;