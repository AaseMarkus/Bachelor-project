import React, { Component } from 'react'
import IndividualShiftImage from './../../../assets/images/registered-shifts.PNG'
import './InformationButtons.css'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class IndividualShift extends Component {
    render() {
        return (
            <Container>
                <Row>
                    <h3>Registrerte vakter</h3>
                </Row>
                <Row>
                    <Image src={IndividualShiftImage} alt="Image of Individual shifts" />
                </Row>
                <Row>
                    Registrerte vakter blir vist i blir vist i dagsboksene med starttid på venstre side, og
                    sluttid eller {'<>'} for pilvakter på høyre side. Vakter som er lagret i databasen vises med
                    svart tekst, mens nye vakter som ikke er lagret blir vist med gul-oransje tekst.
                    På høyre side av vaktene er det et kryss man kan trykke på for å slette vakten
                    hvis dagen ikke er låst og vakten ikke har passert.
                </Row>
            </Container>
            
        )
    }
}
