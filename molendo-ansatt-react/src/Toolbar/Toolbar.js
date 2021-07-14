import React, { Component } from 'react'
import { Navbar, Nav, Dropdown, DropdownButton } from 'react-bootstrap'
import { NavLink } from 'react-router-dom'
import i18n from './../Language/i18n'
import './Toolbar.css'

export default class Toolbar extends Component {
    render() {

        return (
            <Navbar
                variant="dark"
                role="navigation">
                <Navbar.Brand href="/">
                    <div className="logo" alt="service logo"/></Navbar.Brand>
                
                <Nav className="ml-auto">

                <NavLink
                    className="nav-link"
                    to="/home">
                        {i18n.t('nav.home')}
                </NavLink>

                <NavLink
                    className="nav-link"
                    to="/calendar">
                        {i18n.t('nav.calendar')}
                </NavLink>

                    <Nav.Link
                        className="wide"
                        onClick={this.props.logOut}>
                            {i18n.t('nav.log-out')}
                    </Nav.Link>

                    <DropdownButton
                        variant="secondary"
                        menuAlign="right"
                        title={i18n.t('nav.menu')} >
                        <Dropdown.Header tabIndex="-1">{i18n.t('login.menu-language')}</Dropdown.Header>
                        <Dropdown.Item onClick={() => this.props.handleLangChange("en")}>English</Dropdown.Item>
                        <Dropdown.Item onClick={() => this.props.handleLangChange("sw")}>Svenska (N/A)</Dropdown.Item>
                        <Dropdown.Item onClick={() => this.props.handleLangChange("no")}>Norsk</Dropdown.Item>
                        <Dropdown.Divider tabIndex="-1" />
                        <NavLink
                            activeClassName=""
                            className="dropdown-item"
                            to="/information">
                                {i18n.t('nav.information')}
                        </NavLink>
                        <Dropdown.Item className="narrow" onClick={this.props.logOut}>{i18n.t('nav.log-out')}</Dropdown.Item>
                    </DropdownButton>
                </Nav>
            </Navbar>
        )
    }
}
