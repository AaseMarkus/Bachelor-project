import React, { Component } from 'react'
import WeekLockLocked from './../../../assets/images/week-lock-locked.PNG'
import WeekLockUnlocked from './../../../assets/images/week-lock-unlocked.PNG'
import './InformationButtons.css'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class WeekLock extends Component {
    render() {
        return (
            <Row>
                <Col xs={0.1}>
                    <Container>
                        <Row><Image src={WeekLockUnlocked} alt="Image of weekbox" width={200}/></Row>
                        <Row><Image src={WeekLockLocked} alt="Image of weekbox" width={200}/></Row>
                    </Container>

                </Col>
                <Col>
                    <b><i>Låseknappen</i></b> brukes for å deaktivere muligheten for å gjøre endringer i
                    den angitte uken frem til låseknappen trykkes på igjen. Dette kan brukes for
                    for å forsikre at ingen uønskede endringer blir gjort.
                </Col>
            </Row>
        )
    }
}
