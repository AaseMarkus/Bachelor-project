const parseJwt = (token) => {
    var base64Url = token.split('.')[1]
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
  
    return JSON.parse(jsonPayload)
  };
  
  export const loadAuth = () => {
    // loads jwt from cookie, 
    const token = localStorage.getItem('MolendoToken')
    if (!token) return
    const payload = parseJwt(token)
    // A day is subtracted from expiration to reduce chance
    // of untimely auth redirection
    const expiration = new Date(payload.exp*1000-86400000)
    if (expiration.getTime() < new Date().getTime()) {
      localStorage.removeItem('MolendoToken')
      return
    } else {
      return {jwt: token, opServId: payload.opservid}
    }
  }

  export const loadService = () => {
    // loads service info from cookie, 
    const token = localStorage.getItem('MolendoToken')
    if (!token) return {name: "Magic Circle", theme: "magic-circle",
                        minHour: 9, maxHour: 1}
    const payload = parseJwt(token)
    return {name: payload.service, theme: payload.theme,
        minHour: payload.minhour, maxHour: payload.maxhour}
  }