import React, { Component } from 'react'
import DayLockUnlocked from './../../../assets/images/day-lock-unlocked.PNG'
import DayLockLocked from './../../../assets/images/day-lock-locked.PNG'
import './InformationButtons.css'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class DayLock extends Component {
    render() {
        return (
            <Row>
                <Col xs={0.1}>
                    <Container>
                        <Row><Image src={DayLockUnlocked} alt="Image of unlocked day lock icon" height={75} /></Row>
                        <Row><Image src={DayLockLocked} alt="Image of llocked day lock icon" height={75} /></Row>
                    </Container>

                </Col>
                <Col>
                    <b><i>Låseknappen</i></b> i dagsboksene brukes for å deaktivere funksjonaliteten for den angitte datoen.
                </Col>
            </Row>
        )
    }
}
