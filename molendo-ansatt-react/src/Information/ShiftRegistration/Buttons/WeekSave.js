import React, { Component } from 'react'
import WeekSaveImage from './../../../assets/images/week-save.PNG'
import './InformationButtons.css'

import { Container, Row, Col, Image } from 'react-bootstrap'

export default class WeekSave extends Component {
    render() {
        return (
            <Row>
                <Col xs={0.1}>
                    <Image src={WeekSaveImage} alt="Image of weekbox" width={200}/>
                </Col>
                <Col>
                    <b><i>Lagreknappen</i></b> brukes for å lagre de endrede vaktene til databasen.
                </Col>
            </Row>
        )
    }
}
