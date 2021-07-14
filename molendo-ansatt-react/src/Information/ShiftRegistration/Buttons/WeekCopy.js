import React, { Component } from 'react'
import WeekCopyImage from './../../../assets/images/week-copy.PNG'
import './InformationButtons.css'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class WeekCopy extends Component {
    render() {
        return (
            <Row>
                <Col xs={0.1}>
                    <Image src={WeekCopyImage} alt="Image of weekbox" width={200}/>
                </Col>
                <Col>
                    <b><i>Kopieringsknappen</i></b> brukes for å duplisere vaktene i den angitte uken over til andre uker. 
                    Mer om kopiering lenger ned.
                </Col>
            </Row>
        )
    }
}
