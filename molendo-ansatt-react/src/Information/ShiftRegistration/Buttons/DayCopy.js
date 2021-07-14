import React, { Component } from 'react'
import DayCopyImage from './../../../assets/images/day-copy.PNG'
import './InformationButtons.css'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class DayCopy extends Component {
    render() {
        return (
            <Row>
                <Col xs={0.1}>
                    <Image src={DayCopyImage} alt="Image of day copy icon" height={75}/>
                </Col>
                <Col>
                    <b><i>Kopieringsknappen</i></b> brukes for å kopiere over alle vaktene på den angitte datoen til andre datoer. Mer om kopiering lenger ned.
                </Col>
            </Row>
        )
    }
}
