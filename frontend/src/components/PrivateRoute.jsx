import { Navigate } from "react-router-dom"

export const PrivateRoute = ({children}) => {


    if (!user){
        return<Navigate to="/" />
    }


    return children
}