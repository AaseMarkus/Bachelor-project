import React, {Component } from 'react'
import { Redirect } from 'react-router-dom'
import { Container, Row, Col, Toast, Dropdown, DropdownButton } from 'react-bootstrap'
import i18n from './../Language/i18n'
import Spinner from 'react-spinkit'
import LoginPhone from './LoginPhone'
import LoginCode from './LoginCode'
import SettingsIcon from './../Icons/SettingsIcon'
import './Login.css'


export default class Login extends Component {
    constructor() {
        super()

        this.state = {
            onCodePage: false,
            phone: '',
            code: '',
            loading: false,
            invalidPhone: false,
            invalidCode: false,
        }

        this.handleChange = this.handleChange.bind(this)
        this.handleNextClick = this.handleNextClick.bind(this)
        this.handleBackClick = this.handleBackClick.bind(this)
    }

    componentDidMount() {
        this.getServices()
        document.title = i18n.t('title.login')
    }

    handleChange(event) {
        const {name, value} = event.target
        this.setState({ [name]: value })
    }

    handleNextClick(event) {
        const {name} = event.target
        event.preventDefault()
        if(name === 'sendSMS') this.sendPhone()
        else if(name === 'login') this.sendCode()
    }

    handleBackClick(event) {
        this.setState({onCodePage: false})
    }

    getServices() {
        this.setState({loading: true})
        let url = `${this.props.baseUrl}tables/service`
        let options = {
			mode: 'cors',
			headers: {'Access-Control-Allow-Origin': '*'}
		}
        fetch(url, options)
                .then(async response => {
                    this.setState({loading: false})
                    const data = await response.json()
                    if (response.status == 200) {
                        this.props.setServices(data)
                    } else {
                        const error = (data && data.message) || response.status
                        return Promise.reject(error)
                    }
                })
                .catch(error => {
                    this.setState({loading: false})
                    console.log(error)
                }) 
    }


    sendPhone() {
        this.setState({loading: true})
        let url = `${this.props.baseUrl}auth/send-sms-code/`
        let options = {
			mode: 'cors',
			headers: {
				'Access-Control-Allow-Origin': '*',
				'Content-Type': 'application/json'
			},
			method: 'POST',
			body: JSON.stringify({phone: this.state.phone, service: this.props.service})
		}
        fetch(url, options)
                .then(async response => {
                    this.setState({loading: false})

                    const data = await response.json()
                    if (response.status == 200) {
                        this.setState({invalidPhone: false, onCodePage: true})
                    } else if (response.status == 404) {
                        this.setState({invalidPhone: true})
                    } else {
                        const error = (data && data.message) || response.status
                        return Promise.reject(error)
                    }
                })
                .catch(error => {
                    this.setState({loading: false})
                    console.log(error)
                }) 
    }

    sendCode() {
        this.setState({loading: true})
        let url = `${this.props.baseUrl}auth/check-sms-code/`
        let options = {
			mode: 'cors',
			headers: {
				'Access-Control-Allow-Origin':'*',
				'Content-Type': 'application/json'
			},
			method: 'POST',
			body: JSON.stringify({phone: this.state.phone, code: this.state.code})
		}
        fetch(url, options)
                .then(async response => {
                    this.setState({loading: false})
                    const data = await response.json()
                    if (response.status == 200) {
                        this.props.setAuthenticated({jwt: data.token, opServId: data.id})
                        // given as args because state can be slow to update
                        this.props.getRosters(data.id, data.token) 
                    } else if (response.status == 400 || response.status == 401) {
                        this.setState({invalidCode: true})
                    } else {
                        const error = (data && data.message) || response.status
                        return Promise.reject(error)
                    }
                })
                .catch(error => {
                    this.setState({loading: false})
                    console.log(error)
                }) 
    }

    createToast() {
        let msg = ''
        if (this.state.invalidPhone) {  
            msg = i18n.t('login.phone-invalid')
        } else if (this.state.invalidCode) {
            msg = 'Den oppgitte koden er ugyldig'
        }
        return (
            <Toast show={this.state.invalidCode || this.state.invalidPhone}>
                <Toast.Body>
                    {msg}
                </Toast.Body>
            </Toast>
        )
    }
    
    render() {
        let loginComponent
        if (this.state.onCodePage) {
            loginComponent = <LoginCode 
                    phone={this.state.phone} 
                    handleChange={this.handleChange} 
                    handleNextClick={this.handleNextClick}
                    handleBackClick={this.handleBackClick}
                />
        } else {
            loginComponent = <LoginPhone
                    phoneNumber={this.state.phone}
                    handleChange={this.handleChange} 
                    handleNextClick={this.handleNextClick}
                />
        }

        const spinner = <div className="loader">
            <Spinner name="three-bounce"/>
            </div>

        let content = (
            <div>
                <Row noGutters="true" className="justify-content-center">
                    <Col xs="auto" sm="auto">{this.state.loading ? spinner : loginComponent}</Col>
                </Row> 
            </div>
        )
        
        let servicesMenu = [
            <Dropdown.Item key="1" disabled={true}>{i18n.t('login.menu-service')}
            </Dropdown.Item>,
            this.props.services ? Object.entries(this.props.services).map( ([k, v]) =>
                <Dropdown.Item
                    key={k}
                    disabled={this.state.onCodePage}
                    onClick={() => this.props.handleServiceChange(k)}>{k}
                </Dropdown.Item>) : null,
            <Dropdown.Divider key="2" />
        ]
        
        let menu = (
            <DropdownButton
                disabled={this.state.loading}
                variant="secondary"
                className="loginMenu"
                title={<SettingsIcon />}>
                {!this.state.onCodePage ? servicesMenu : null}
                <Dropdown.Item disabled={true}>{i18n.t('login.menu-language')}</Dropdown.Item>
                <Dropdown.Item onClick={() => this.props.handleLangChange("en")}>English</Dropdown.Item>
                <Dropdown.Item onClick={() => this.props.handleLangChange("sw")}>Svenska (N/A)</Dropdown.Item>
                <Dropdown.Item onClick={() => this.props.handleLangChange("no")}>Norsk</Dropdown.Item>
            </DropdownButton>
        )
        const toast = this.createToast()

        return (
            <div className="login">
                {this.props.authenticated && <Redirect to="/home" />}
                <Container
                    role="main">
                    <Row
                        role="banner"
                        noGutters="true"
                        className="justify-content-center">
                        <Col
                            alt="service logo"
                            className="logo"
                            xs="auto"
                            sm="auto">
                        </Col>
                    </Row>
                    {content}
                    <Row
                        role="form"
                        noGutters="true"
                        className="justify-content-center">
                        <Col xs="auto" sm="auto">
                            {toast}
                        </Col>
                    </Row>
                </Container>
                {menu}
            </div>
        )
    }
}