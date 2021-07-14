import React, { Component } from 'react'
import WeekResetImage from './../../../assets/images/week-reset.PNG'
import './InformationButtons.css'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class WeekReset extends Component {
    render() {
        return (
            <Row>
                <Col xs={0.1}>
                    <Image src={WeekResetImage} alt="Image of weekbox" width={200} />
                </Col>
                <Col>
                    <b><i>Nullstillknappen</i></b> brukes for å slette alle vaktene som er registrert på den angitte uken
                </Col>
            </Row>
        )
    }
}
