import React, { Component } from 'react'
import DayResetImage from './../../../assets/images/day-reset.PNG'
import './InformationButtons.css'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class DayReset extends Component {
    render() {
        return (
            <Row>
                <Col xs={0.1}>
                    <Image src={DayResetImage} alt="Image of day reset icon" height={75} />
                </Col>
                <Col>
                    <b><i>Nullstillingsknappen</i></b> brukes for å slette alle vakter på den angitte datoen som ikke allerede har passert.
                </Col>
            </Row>
        )
    }
}
