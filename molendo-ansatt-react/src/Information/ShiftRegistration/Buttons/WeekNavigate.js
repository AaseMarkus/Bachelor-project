import React, { Component } from 'react'
import WeekNavigateImage from './../../../assets/images/week-navigate.PNG'
import './InformationButtons.css'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class WeekNavigate extends Component {
    render() {
        return (
            <Row>
                <Col xs={0.1}>
                    <Image src={WeekNavigateImage} alt="Image of weekbox" width={200}/>
                </Col>
                <Col>
                    <b><i>Navigeringsknappene</i></b> brukes for å bevege seg mellom ukedagene hvis skjermen er for
                    liten til å vise alle på en gang.
                </Col>
            </Row>
        )
    }
}
