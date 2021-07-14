import React, { Component } from 'react'
import { Container, Row, Col } from 'react-bootstrap'
import i18n from './../Language/i18n'
import ShiftRegistration from './ShiftRegistration/ShiftRegistration'
import Acknowledgements from './Acknowledgements/Acknowledgements'
import './Information.css'

export default class Information extends Component {

    componentDidMount() {
        document.title = i18n.t('title.information')
    }

    render() {
        return (
            <div className="informationContainer" role="main">
                <Container className="headerLinksContainer">
                    <Row><h4><a href="#shiftRegistration">{i18n.t('information.shiftregistrations-title')} &gt;</a></h4></Row>
                    <Row><h4><a href="#acknowledgements">{i18n.t('information.acknowledgements-title')} &gt;</a></h4></Row>
                </Container>
                <ShiftRegistration />
                <Acknowledgements />
            </div>
            
        )
    }
}
